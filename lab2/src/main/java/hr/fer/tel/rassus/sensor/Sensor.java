package hr.fer.tel.rassus.sensor;

import hr.fer.tel.rassus.clock.EmulatedSystemClock;
import hr.fer.tel.rassus.kafka.KafkaConsumerService;
import hr.fer.tel.rassus.kafka.KafkaProducerService;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class Sensor {

    private final String sensor_id;
    private static boolean running = false;
    private static boolean finished = false;
    private static boolean registered = false;

    private final KafkaProducerService producer;
    private final KafkaConsumerService command_consumer;
    private final KafkaConsumerService register_consumer;
    private final KafkaConsumerService readings_consumer;

    private final Map<String, Integer> vectorClock = new HashMap<>();

    private final EmulatedSystemClock clock = new EmulatedSystemClock();

    private final ReadingProvider readingProvider;

    private final List<SensorReading> collected_readings = new ArrayList<>();

    private static final Logger logger = Logger.getLogger(Sensor.class.getName());

    public Sensor(String sensor_id) throws IOException {
        this.sensor_id = sensor_id;
        this.vectorClock.put(sensor_id, 0);
        String group_id = "sensor=" + sensor_id + "=" + UUID.randomUUID();
        this.producer = new KafkaProducerService();
        this.command_consumer = new KafkaConsumerService(group_id + "-command", ConsumerTopics.COMMAND.getTopicName());
        this.register_consumer = new KafkaConsumerService(group_id + "-register", ConsumerTopics.REGISTER.getTopicName());
        this.readings_consumer = new KafkaConsumerService(group_id + "-readings", ConsumerTopics.READINGS.getTopicName());
        this.command_consumer.initialize();
        this.register_consumer.initialize();
        this.readings_consumer.initialize();
        this.readingProvider = new ReadingProvider("readings.csv");
    }

    public void register() {
        JSONObject json_register = new JSONObject();
        json_register.put("id", sensor_id);
        producer.send("Register", null, json_register.toString());
    }

    public void send_reading(Double value) {
        if (value == null) {
            logger.warning("NO2 reading is missing. Skipping this reading.");
            return;
        }

        vectorClock.put(
                sensor_id,
                vectorClock.get(sensor_id) + 1
        );

        SensorReading reading = new SensorReading(
                sensor_id,
                value,
                clock.currentTimeMillis(),
                new HashMap<>(vectorClock)
        );

        collected_readings.add(reading);

        JSONObject reading_json = new JSONObject();
        reading_json.put("id", reading.getSensorId());
        reading_json.put("reading", reading.getReading());
        reading_json.put("scalarTimestamp", reading.getScalarTimestamp());
        reading_json.put("vectorTimestamp", reading.getVectorTimestamp());

        producer.send("Readings", sensor_id, reading_json.toString());
    }

    private void print_sorted_by_scalar_timestamp() {
        List<SensorReading> sorted = new ArrayList<>(collected_readings);

        sorted.sort(Comparator.comparingLong(
                SensorReading::getScalarTimestamp
        ));

        logger.info("Readings sorted by scalar timestamp:");

        for (SensorReading reading : sorted) {
            logger.info(
                    "Sensor: " + reading.getSensorId()
                            + ", Reading: " + reading.getReading()
                            + ", Scalar timestamp: " + reading.getScalarTimestamp()
            );
        }
    }

    private void print_sorted_by_vector_timestamp() {
        List<SensorReading> sorted = new ArrayList<>(collected_readings);

        sorted.sort((r1, r2) -> {
            Map<String, Integer> v1 = r1.getVectorTimestamp();
            Map<String, Integer> v2 = r2.getVectorTimestamp();

            for (String id : v1.keySet()) {
                int value1 = v1.getOrDefault(id, 0);
                int value2 = v2.getOrDefault(id, 0);

                if (value1 < value2) {
                    return -1;
                } else if (value1 > value2) {
                    return 1;
                }
            }

            return 0;
        });

        logger.info("Readings sorted by vector timestamp:");

        for (SensorReading reading : sorted) {
            logger.info(
                    "Sensor: " + reading.getSensorId()
                            + ", Reading: " + reading.getReading()
                            + ", Vector timestamp: " + reading.getVectorTimestamp()
            );
        }
    }

    public void stop() {
        print_sorted_by_scalar_timestamp();
        print_sorted_by_vector_timestamp();
        producer.close();
        command_consumer.close();
        register_consumer.close();
        readings_consumer.close();
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            logger.severe("Sensor ID is required as a command line argument.");
            return;
        }

        String sensorId = args[0];
        Sensor sensor = new Sensor(sensorId);

        logger.info("Sensor " + sensorId + " initialized. Waiting for commands...");

        long last_print_time = System.nanoTime();

        while(!finished) {
            sensor.command_consumer.listen(message -> {
                JSONObject json_command = new JSONObject(message);
                String command_type = json_command.getString("type");

                if (command_type.equals("Start")) {
                    running = true;
                    logger.info("Sensor " + sensorId + " started.");
                } else if (command_type.equals("Stop")) {
                    running = false;
                    finished = true;
                    logger.info("Sensor " + sensorId + " stopped.");
                }
            });

            if(running && !registered) {
                sensor.register();
                registered = true;
            }

            if(running) {
                sensor.register_consumer.listen(message -> {
                    JSONObject message_json = new JSONObject(message);
                    String id = message_json.getString("id");
                    sensor.vectorClock.put(id, 0);
                    logger.info("Sensor " + sensorId + " received registration confirmation: sensor " + id);
                });

                Double new_reading = sensor.readingProvider.get_next_reading();

                sensor.send_reading(new_reading);

                sensor.readings_consumer.listen(message -> {
                    JSONObject message_json = new JSONObject(message);
                    String sensor_id = message_json.getString("id");
                    double reading = message_json.getDouble("reading");
                    long scalar_timestamp = message_json.getLong("scalarTimestamp");
                    JSONObject vector_timestamp = message_json.getJSONObject("vectorTimestamp");;

                    for (String id : vector_timestamp.keySet()) {
                        sensor.vectorClock.putIfAbsent(id, 0);
                    }

                    sensor.vectorClock.putIfAbsent(sensor_id, 0);

                    for (String id : sensor.vectorClock.keySet()) {
                        int received_value = vector_timestamp.optInt(id, 0);

                        sensor.vectorClock.compute(id, (k, local_value) -> Math.max(local_value, received_value));
                    }

                    sensor.vectorClock.put(
                            sensorId,
                            sensor.vectorClock.get(sensorId) + 1
                    );

                    Map<String, Integer> received_vector = new HashMap<>();

                    for (String id : vector_timestamp.keySet()) {
                        received_vector.put(id, vector_timestamp.getInt(id));
                    }

                    if (!sensor_id.equals(sensorId)) {
                        SensorReading received_reading = new SensorReading(
                                sensor_id,
                                reading,
                                scalar_timestamp,
                                received_vector
                        );

                        sensor.collected_readings.add(received_reading);
                    }

                    logger.info("Sensor " + sensorId + " received reading: " + message_json);
                });

                long currentTime = System.nanoTime();

                if (currentTime - last_print_time >= 5_000_000_000L) {
                    sensor.print_sorted_by_scalar_timestamp();
                    sensor.print_sorted_by_vector_timestamp();

                    sensor.collected_readings.clear();
                    last_print_time = currentTime;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.warning("Sensor " + sensorId + " interrupted during sleep.");
                    Thread.currentThread().interrupt();
                }
            }
        }

        sensor.stop();
    }
}
