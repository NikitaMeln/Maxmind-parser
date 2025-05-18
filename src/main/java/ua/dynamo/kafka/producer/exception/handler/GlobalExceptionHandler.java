package ua.dynamo.kafka.producer.exception.handler;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import ua.dynamo.kafka.producer.exception.*;
import ua.dynamo.kafka.producer.exception.payload.GenericErrorResponse;

import java.time.ZonedDateTime;
import java.util.Collections;

public class GlobalExceptionHandler {

    public static void register(Javalin app) {
        app.exception(DynamoDBQueryException.class, (e, ctx) ->
                handleException(ctx, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage())
        );

        app.exception(DynamoDBNotFoundException.class, (e, ctx) ->
                handleException(ctx, HttpStatus.NOT_FOUND, e.getMessage())
        );

        app.exception(CIDRRangeException.class, (e, ctx) ->
                handleException(ctx, HttpStatus.BAD_REQUEST, e.getMessage())
        );

        app.exception(InitPropertyException.class, (e, ctx) ->
                handleException(ctx, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage())
        );

        // Fallback for all unhandled exceptions
        app.exception(Exception.class, (e, ctx) ->
                handleException(ctx, HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error: " + e.getMessage())
        );
    }

    private static void handleException(Context ctx, HttpStatus status, String message) {
        GenericErrorResponse response = new GenericErrorResponse();
        response.setTimestamp(ZonedDateTime.now());
        response.setStatus(status.getCode());
        response.setError(status.getMessage());
        response.setPath(ctx.path());
        response.setErrorMessages(Collections.singletonList(message));

        ctx.status(status);
        ctx.json(response);
    }
}
