package org.example.springboot_rabbitmq.dlq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DlqConfig {

    // Nombres del flujo normal
    public static final String EXCHANGE_NORMAL = "exchange_normal";
    public static final String QUEUE_NORMAL = "queue_normal";
    public static final String ROUTING_NORMAL = "rutina.normal";

    // Nombres del flujo de rescate (Dead Letter)
    public static final String EXCHANGE_DLX = "exchange_dlx"; // Dead Letter Exchange
    public static final String QUEUE_DLQ = "queue_dlq";       // Dead Letter Queue
    public static final String ROUTING_DLQ = "rutina.dlq";

    // 1. Creamos ambos Exchanges (usaremos Direct para enrutamiento exacto)
    @Bean
    public DirectExchange normalExchange() {
        return new DirectExchange(EXCHANGE_NORMAL);
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(EXCHANGE_DLX);
    }

    // 2. Creamos la Cola de Rescate (DLQ) - Es una cola completamente normal
    @Bean
    public Queue dlqQueue() {
        return QueueBuilder.durable(QUEUE_DLQ).build();
    }

    // 3. Creamos la Cola Normal (AQUÍ ESTÁ LA MAGIA DE LA DLQ)
    @Bean
    public Queue normalQueue() {
        return QueueBuilder.durable(QUEUE_NORMAL)
                // Si un mensaje muere aquí, mándalo a este Exchange:
                .withArgument("x-dead-letter-exchange", EXCHANGE_DLX)
                // Y ponle esta nueva etiqueta para que el DLX sepa a qué cola mandarlo:
                .withArgument("x-dead-letter-routing-key", ROUTING_DLQ)
                .build();
    }

    // 4. Tiramos los cables
    @Bean
    public Binding normalBinding(Queue normalQueue, DirectExchange normalExchange) {
        return BindingBuilder.bind(normalQueue).to(normalExchange).with(ROUTING_NORMAL);
    }

    @Bean
    public Binding dlqBinding(Queue dlqQueue, DirectExchange dlxExchange) {
        return BindingBuilder.bind(dlqQueue).to(dlxExchange).with(ROUTING_DLQ);
    }
}