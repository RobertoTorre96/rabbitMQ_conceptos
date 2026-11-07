package org.example.springboot_rabbitmq.idempotencia;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdempotenciaConfig {

    public static final String EXCHANGE = "exchange_idempotencia";
    public static final String QUEUE_PAGOS = "queue_idempotencia_pagos";
    public static final String ROUTING_KEY = "rutina.idempotencia";

    @Bean
    public DirectExchange idempotenciaExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue idempotenciaQueue() {
        return new Queue(QUEUE_PAGOS, true);
    }

    @Bean
    public Binding idempotenciaBinding(Queue idempotenciaQueue, DirectExchange idempotenciaExchange) {
        return BindingBuilder.bind(idempotenciaQueue).to(idempotenciaExchange).with(ROUTING_KEY);
    }
}