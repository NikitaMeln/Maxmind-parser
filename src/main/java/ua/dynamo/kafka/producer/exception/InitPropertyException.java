package ua.dynamo.kafka.producer.exception;

public class InitPropertyException extends RuntimeException {
    public InitPropertyException(String message) {
        super(message);
    }

    public InitPropertyException(String message, Throwable cause) {
        super(message, cause);
    }
}
