package org.example.springboot_rabbitmq.retry;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RetryConfig {

    public static final String EXCHANGE_RETRY = "exchange_retry";
    public static final String QUEUE_RETRY = "queue_retry";
    public static final String ROUTING_RETRY = "rutina.retry";

    public static final String EXCHANGE_RETRY_DLX = "exchange_retry_dlx";
    public static final String QUEUE_RETRY_DLQ = "queue_retry_dlq";
    public static final String ROUTING_RETRY_DLQ = "rutina.retry.dlq";

    @Bean
    public DirectExchange retryExchange() { return new DirectExchange(EXCHANGE_RETRY); }

    @Bean
    public DirectExchange retryDlxExchange() { return new DirectExchange(EXCHANGE_RETRY_DLX); }

    @Bean
    public Queue retryDlqQueue() { return QueueBuilder.durable(QUEUE_RETRY_DLQ).build(); }

    @Bean
    public Queue retryQueue() {
        return QueueBuilder.durable(QUEUE_RETRY)
                .withArgument("x-dead-letter-exchange", EXCHANGE_RETRY_DLX)
                .withArgument("x-dead-letter-routing-key", ROUTING_RETRY_DLQ)
                .build();
    }

    @Bean
    public Binding bindingRetry(Queue retryQueue, DirectExchange retryExchange) {
        return BindingBuilder.bind(retryQueue).to(retryExchange).with(ROUTING_RETRY);
    }

    @Bean
    public Binding bindingRetryDlq(Queue retryDlqQueue, DirectExchange retryDlxExchange) {
        return BindingBuilder.bind(retryDlqQueue).to(retryDlxExchange).with(ROUTING_RETRY_DLQ);
    }
}