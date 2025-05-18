package ua.dynamo.kafka.producer.exception;

public class KafkaProcessingException extends RuntimeException {
    public KafkaProcessingException(String message) {
        super(message);
    }

    public KafkaProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
