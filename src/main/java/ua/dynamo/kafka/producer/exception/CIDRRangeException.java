package ua.dynamo.kafka.producer.exception;

public class CIDRRangeException extends RuntimeException {
    public CIDRRangeException(String message) {
        super(message);
    }

    public CIDRRangeException(String message, Throwable cause) {
        super(message, cause);
    }
}
