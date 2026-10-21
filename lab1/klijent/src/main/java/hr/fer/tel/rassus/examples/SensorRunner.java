package hr.fer.tel.rassus.examples;

import java.util.logging.Logger;

public class SensorRunner {

    private static final Logger logger =
            Logger.getLogger(SensorRunner.class.getName());

    private static SensorData toSensorData(ReadingsResponse response) {
        return new SensorData(
                response.hasTemperature() ? response.getTemperature() : null,
                response.hasPressure() ? response.getPressure() : null,
                response.hasHumidity() ? response.getHumidity() : null,
                response.hasCo() ? response.getCo() : null,
                response.hasNo2() ? response.getNo2() : null,
                response.hasSo2() ? response.getSo2() : null
        );
    }

    public static void main(String[] args) throws Exception {
        String ip = "127.0.0.1";
        int port = args.length > 0
                ? Integer.parseInt(args[0])
                : 50051;

        ReadingsProvider readingsProvider = new ReadingsProvider();
        SensorService service = new SensorService(readingsProvider);

        SensorRPCServer server = new SensorRPCServer(service, port);
        server.start();

        SensorRPCClient neighborClient = null;

        try {
            SensorRegistrationClient registrationClient =
                    new SensorRegistrationClient();

            long sensorId = registrationClient.registerSensor(ip, port);

            logger.info("Registrirani senzor ID: " + sensorId);

            SensorInfo nearestSensor = registrationClient.findNearestSensor(sensorId);

            if(nearestSensor != null) {
                logger.info("Najblizi senzor: " + nearestSensor);
            } else {
                logger.info("Nema dostupnih senzora.");
            }

            if (nearestSensor != null) {
                neighborClient = new SensorRPCClient(
                        nearestSensor.ip(),
                        nearestSensor.port()
                );
            }

            while (!Thread.currentThread().isInterrupted()) {
                SensorData ownReadings = readingsProvider.getCurrentReadings();

                SensorData calibratedReadings = ownReadings;

                if (neighborClient != null) {
                    try {
                        ReadingsResponse response = neighborClient.requestReadings();

                        SensorData nearestSensorReadings = toSensorData(response);

                        calibratedReadings = ownReadings.calibrate(nearestSensorReadings);

                    } catch (Exception e) {
                        logger.warning(
                                "Susjed nije dostupan; salju se vlastita ocitanja: "
                                        + e.getMessage()
                        );
                    }
                }

                try {
                    registrationClient.sendReading(sensorId, calibratedReadings);

                    logger.info("Ocitanje uspjesno poslano posluzitelju.");
                } catch (Exception e) {
                    logger.warning(
                            "Slanje ocitanja nije uspjelo: " + e.getMessage()
                    );
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } finally {
            if (neighborClient != null) {
                neighborClient.stop();
            }
            server.stop();
        }
    }
}
