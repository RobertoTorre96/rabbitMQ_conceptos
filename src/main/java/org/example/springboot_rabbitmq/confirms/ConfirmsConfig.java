package org.example.springboot_rabbitmq.confirms;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfirmsConfig {

    public static final String EXCHANGE_CONFIRMS = "exchange_confirms";
    public static final String QUEUE_CONFIRMS = "queue_confirms";
    public static final String ROUTING_CONFIRMS = "rutina.confirms";

    @Bean
    public DirectExchange confirmsExchange() { return new DirectExchange(EXCHANGE_CONFIRMS); }

    @Bean
    public Queue confirmsQueue() { return new Queue(QUEUE_CONFIRMS, true); }

    @Bean
    public Binding bindingConfirms(Queue confirmsQueue, DirectExchange confirmsExchange) {
        return BindingBuilder.bind(confirmsQueue).to(confirmsExchange).with(ROUTING_CONFIRMS);
    }
}