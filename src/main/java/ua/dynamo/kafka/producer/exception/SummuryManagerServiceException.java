package ua.dynamo.kafka.producer.exception;

public class SummuryManagerServiceException extends RuntimeException {
    public SummuryManagerServiceException(String message) {
        super(message);
    }

    public SummuryManagerServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
