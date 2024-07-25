package com.megaloans.lms.config;

public class KafkaConfig {
//
//    Robust Kafka Consumer Error Handling in Spring Boot 3
//    The article discusses how to implement robust error handling in a Kafka consumer for a Spring Boot 3 application. It covers:
//
//    Blocking Retry: How to retry consuming a message when an exception occurs, and block the next message until the retry is successful.
//            Non-Blocking Retry: How to send the message to another retry topic when the blocking retry max attempts limit is exceeded, using the @RetryableTopic annotation.
//    Dead Letter Queue: How to send the message to a dead letter topic when the non-blocking retry max attempts limit is exceeded or the exception is not retriable.
//
//    The article provides code examples and configurations for each scenario, including how to use the @RetryableTopic annotation, KafkaListener method, and DltHandler method.
//    Key Takeaways
//
//    Use @RetryableTopic for non-blocking retry mechanism
//    Configure blocking retries using BlockingRetriesConfigurer
//    Use DltHandler for dead letter queue handling
//    Customize retry attempts, intervals, and exception handling using various properties and configurations
//
//    Summary
//    The article provides a comprehensive guide to implementing robust error handling in a Kafka consumer for a Spring Boot 3 application, covering blocking retry, non-blocking retry, and dead letter queue mechanisms.


}
