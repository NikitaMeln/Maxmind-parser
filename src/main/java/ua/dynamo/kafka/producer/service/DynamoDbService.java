package ua.dynamo.kafka.producer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import ua.dynamo.kafka.producer.exception.DynamoDBQueryException;
import ua.dynamo.kafka.producer.mapper.TableDataMapper;
import ua.dynamo.kafka.producer.model.SummaryInfo;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class DynamoDbService {

    private final String tableName;
    private final String keyCondition;

    private final CIDRRangeService cidrRangeService;
    private final DynamoDbAsyncClient dynamoDbAsyncClient;
    private final TableDataMapper tableDataMapper;

    private static final Logger logger = LoggerFactory.getLogger(DynamoDbService.class);

    public DynamoDbService(
            String tableName,
            String keyCondition,
            CIDRRangeService cidrRangeService,
            DynamoDbAsyncClient dynamoDbAsyncClient,
            TableDataMapper tableDataMapper
    ) {
        this.tableName = tableName;
        this.keyCondition = keyCondition;
        this.cidrRangeService = cidrRangeService;
        this.dynamoDbAsyncClient = dynamoDbAsyncClient;
        this.tableDataMapper = tableDataMapper;
    }

    public List<SummaryInfo> getSummaryInfo(String ip) {
        List<String> cidrList = cidrRangeService.getIpRange(ip);
        Map<String, Map<String, AttributeValue>> resultMap = new ConcurrentHashMap<>();

        try {
            List<CompletableFuture<Void>> futures = cidrList.stream()
                    .map(record -> {
                        QueryRequest queryRequest = QueryRequest.builder()
                                .tableName(tableName)
                                .keyConditionExpression(keyCondition + " = :" + keyCondition)
                                .expressionAttributeValues(Collections.singletonMap(
                                        ":" + keyCondition,
                                        AttributeValue.builder().s(record).build()
                                ))
                                .build();

                        return dynamoDbAsyncClient.query(queryRequest)
                                .thenAccept(response -> {
                                    if (!response.items().isEmpty()) {
                                        response.items().forEach(item -> resultMap.put(record, item));
                                    }
                                })
                                .exceptionally(ex -> {
                                    logger.debug("Error querying DynamoDB for record {}: {}", record, ex.getMessage());
                                    throw new DynamoDBQueryException("Query error: " + ex.getMessage());
                                });
                    })
                    .toList();

            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allOf.join();

            return searchCIDRs(resultMap);
        } catch (Exception e) {
            throw new DynamoDBQueryException("DynamoDB query failed: " + e.getMessage());
        }
    }

    private List<SummaryInfo> searchCIDRs(Map<String, Map<String, AttributeValue>> resultMap) {
        return resultMap.values().stream()
                .map(tableDataMapper::parseSummaryInfo)
                .toList();
    }
}
