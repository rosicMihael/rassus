package hr.fer.tel.rassus.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class KafkaProducerService {
    private final Producer<String, String> producer;

    public KafkaProducerService() {
        Properties properties = new Properties();

        properties.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092"
        );
        properties.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class
        );
        properties.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class
        );

        this.producer = new KafkaProducer<>(properties);
    }

    public void send(String topic, String key, String message) {
        ProducerRecord<String, String> record =
                new ProducerRecord<>(topic, key, message);

        producer.send(record);
    }

    public void close() {
        producer.close();
    }
}
