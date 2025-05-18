package ua.dynamo.kafka.producer.exception;

public class DynamoDBQueryException extends RuntimeException {
    public DynamoDBQueryException(String message) {
        super(message);
    }

    public DynamoDBQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
