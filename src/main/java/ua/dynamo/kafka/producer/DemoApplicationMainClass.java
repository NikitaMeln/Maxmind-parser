package ua.dynamo.kafka.producer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.javalin.Javalin;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import ua.dynamo.kafka.producer.controller.InformationByIpController;
import ua.dynamo.kafka.producer.exception.handler.GlobalExceptionHandler;
import ua.dynamo.kafka.producer.mapper.ExportDataMapper;
import ua.dynamo.kafka.producer.mapper.TableDataMapper;
import ua.dynamo.kafka.producer.service.CIDRRangeService;
import ua.dynamo.kafka.producer.service.DynamoDbService;
import ua.dynamo.kafka.producer.service.KafkaProducerService;
import ua.dynamo.kafka.producer.service.SummaryManagerService;

public class DemoApplicationMainClass {
    public static void main(String[] args) {
        // Configuration parameters
        String tableName = System.getenv().getOrDefault("DYNAMO_TABLE", "YourDefaultTableName");
        String keyColumn = "cidr";
        String topic = "your-topic";

        // Kafka producer configuration
        Properties kafkaProps = new Properties();
        kafkaProps.put("bootstrap.servers", "localhost:9092");
        kafkaProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> producer = new KafkaProducer<>(kafkaProps);

        // ObjectMapper configuration for ISO datetime
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(
                LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"))
        );
        objectMapper.registerModule(javaTimeModule);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        KafkaProducerService kafkaService = new KafkaProducerService(topic, producer, objectMapper);
        CIDRRangeService cidrService = new CIDRRangeService();
        ExportDataMapper exportMapper = new ExportDataMapper();
        TableDataMapper tableMapper = new TableDataMapper();
        DynamoDbAsyncClient dynamoClient = DynamoDbAsyncClient.create();

        DynamoDbService dynamoDbService = new DynamoDbService(
                tableName,
                keyColumn,
                cidrService,
                dynamoClient,
                tableMapper
        );

        SummaryManagerService summaryService = new SummaryManagerService(
                dynamoDbService,
                exportMapper,
                cidrService,
                kafkaService
        );

        // Start Javalin server
        Javalin app = Javalin.create(config -> {
            config.http.generateEtags = true;       
            config.http.prefer405over404 = true;   
            config.http.maxRequestSize = 5000;        
            config.http.disableCompression();
        }).start(8080);

        GlobalExceptionHandler.register(app);

        // Register controller
        InformationByIpController controller = new InformationByIpController(summaryService);
        controller.registerRoutes(app);

        System.out.println("✅ Server started on http://localhost:8080");
    }
}