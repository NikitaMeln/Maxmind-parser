package ua.dynamo.kafka.producer.exception.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO representing a generic error response returned to the client.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericErrorResponse {

    private ZonedDateTime timestamp;

    private Integer status;

    private String error = "Error string";

    private List<String> errorMessages;

    private Map<String, String> fieldErrors;

    private String path = "Request path";
}