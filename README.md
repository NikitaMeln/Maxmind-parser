## Project Description

This application exposes a single HTTP endpoint that acts as a trigger for data processing. Upon receiving a request, it performs the following steps:

1. **Retrieves data from DynamoDB** based on the request parameters.
2. **Transforms and maps** the data into a specific structure required for downstream systems.
3. **Publishes the processed data to a Kafka topic** for further consumption.

The project is designed for simplicity, performance, and modularity, using Javalin as the web framework, AWS SDK for DynamoDB integration, and Kafka client for message publishing.

## Technologies

- **Javalin** — a lightweight web framework for building REST APIs.
- **AWS SDK (DynamoDB & Kafka)** — integration with Amazon DynamoDB and Kafka.
- **Slf4j** — logging.
- **Lombok** — simplifies Java code with annotations.
- **JUnit** — for unit testing.