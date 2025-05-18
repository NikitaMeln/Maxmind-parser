package ua.dynamo.kafka.producer.exception;

public class DynamoDBNotFoundException extends RuntimeException {
    public DynamoDBNotFoundException(String message) {
        super(message);
    }
}
