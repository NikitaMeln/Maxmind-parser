package ua.dynamo.kafka.producer.exception;

public class ServePixelGifException extends RuntimeException {
    public ServePixelGifException(String message) {
        super(message);
    }

    public ServePixelGifException(String message, Throwable cause) {
        super(message, cause);
    }

}
