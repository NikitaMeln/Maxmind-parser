package ua.dynamo.kafka.producer.service;

import ua.dynamo.kafka.producer.exception.SummuryManagerServiceException;
import ua.dynamo.kafka.producer.mapper.ExportDataMapper;
import ua.dynamo.kafka.producer.model.ExportPayload;
import ua.dynamo.kafka.producer.model.SummaryInfo;

import java.util.List;
import java.util.Map;

public class SummaryManagerService {

    private final DynamoDbService dynamoDbService;
    private final ExportDataMapper exportDataMapper;
    private final CIDRRangeService cidrRangeService;
    private final KafkaProducerService kafkaProducerService;

    public SummaryManagerService(DynamoDbService dynamoDbService,
                                 ExportDataMapper exportDataMapper,
                                 CIDRRangeService cidrRangeService,
                                 KafkaProducerService kafkaProducerService) {
        this.dynamoDbService = dynamoDbService;
        this.exportDataMapper = exportDataMapper;
        this.cidrRangeService = cidrRangeService;
        this.kafkaProducerService = kafkaProducerService;
    }

    public SummaryInfo getSummaryInfo(String userIp) {
        List<SummaryInfo> summaryInfoList = dynamoDbService.getSummaryInfo(userIp);

        for (SummaryInfo summaryInfoRecord : summaryInfoList) {
            if (summaryInfoRecord != null && cidrRangeService.isIpInRange(summaryInfoRecord.getNetwork(), userIp)) {
                return summaryInfoRecord;
            }
        }
        throw new SummuryManagerServiceException("No such IP found");
    }

    public void exportDataToKafka(ExportPayload payload) {
        try {
            Map<String, Object> recordRow = exportDataMapper.serializeToExport(payload);
            
            kafkaProducerService.sendDataToKafka(recordRow);
        } catch (Exception e) {
            throw new SummuryManagerServiceException("Can't send data to Kafka", e);
        }
    }

    public ExportPayload initPayload(SummaryInfo summaryInfo, String ipAddress) {
        return ExportPayload.builder()
                .summaryInfo(summaryInfo)
                .ipAddress(ipAddress)
                .build();
    }
}
