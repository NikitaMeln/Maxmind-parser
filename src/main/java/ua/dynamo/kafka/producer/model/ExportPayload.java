package ua.dynamo.kafka.producer.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class ExportPayload {
    private SummaryInfo summaryInfo;
    private String ipAddress;
}
