package org.example.springboot_rabbitmq.concurrency;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConcurrencyConfig {

    public static final String EXCHANGE_CONCURRENCY = "exchange_concurrency";
    public static final String QUEUE_CONCURRENCY = "queue_concurrency";
    public static final String ROUTING_CONCURRENCY = "rutina.concurrency";

    @Bean
    public DirectExchange concurrencyExchange() { return new DirectExchange(EXCHANGE_CONCURRENCY); }

    @Bean
    public Queue concurrencyQueue() { return new Queue(QUEUE_CONCURRENCY, true); }

    @Bean
    public Binding bindingConcurrency(Queue concurrencyQueue, DirectExchange concurrencyExchange) {
        return BindingBuilder.bind(concurrencyQueue).to(concurrencyExchange).with(ROUTING_CONCURRENCY);
    }
}