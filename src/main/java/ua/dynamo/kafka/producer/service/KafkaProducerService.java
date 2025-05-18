package ua.dynamo.kafka.producer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.dynamo.kafka.producer.exception.KafkaProcessingException;

import java.util.Map;

public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    private final String topic;
    private final KafkaProducer<String, String> producer;
    private final ObjectMapper objectMapper;

    public KafkaProducerService(String topic,
                                KafkaProducer<String, String> producer,
                                ObjectMapper objectMapper) {
        this.topic = topic;
        this.producer = producer;
        this.objectMapper = objectMapper;
    }

    public void sendDataToKafka(Map<String, Object> input) {
        if (input == null) {
            throw new IllegalArgumentException("Input map cannot be null");
        }

        String exportJson = serializeToJson(input);

        try {
            logger.debug("Serialized to JSON: {}", exportJson);
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, exportJson);

            producer.send(record, (RecordMetadata metadata, Exception exception) -> {
                if (exception != null) {
                    logger.error("Error sending message to Kafka", exception);
                } else {
                    logger.debug("Message sent to topic {} on partition {} with offset {}",
                            metadata.topic(), metadata.partition(), metadata.offset());
                }
            });
        } catch (Exception e) {
            throw new KafkaProcessingException("An error occurred while producing to Kafka", e);
        }
    }

    private String serializeToJson(Map<String, Object> data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            throw new KafkaProcessingException("Failed to serialize data to JSON", e);
        }
    }
}
