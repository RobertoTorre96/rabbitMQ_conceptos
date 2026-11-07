package org.example.springboot_rabbitmq.delayed;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DelayedConfig {

    // Nombres para el destino real
    public static final String EXCHANGE_DESTINO = "exchange_destino";
    public static final String QUEUE_DESTINO = "queue_destino";
    public static final String ROUTING_DESTINO = "rutina.destino";

    // Nombre de la sala de espera
    public static final String QUEUE_ESPERA = "queue_sala_espera";

    // ==========================================
    // 1. EL DESTINO FINAL (Donde trabaja el dev)
    // ==========================================
    @Bean
    public DirectExchange destinoExchange() {
        return new DirectExchange(EXCHANGE_DESTINO);
    }

    @Bean
    public Queue destinoQueue() {
        return new Queue(QUEUE_DESTINO, true);
    }

    @Bean
    public Binding bindingDestino(Queue destinoQueue, DirectExchange destinoExchange) {
        return BindingBuilder.bind(destinoQueue).to(destinoExchange).with(ROUTING_DESTINO);
    }

    // ==========================================
    // 2. LA SALA DE ESPERA (La máquina del tiempo)
    // ==========================================
    @Bean
    public Queue esperaQueue() {
        // Al morir por tiempo, el mensaje será teletransportado al EXCHANGE_DESTINO
        return QueueBuilder.durable(QUEUE_ESPERA)
                .withArgument("x-dead-letter-exchange", EXCHANGE_DESTINO)
                .withArgument("x-dead-letter-routing-key", ROUTING_DESTINO)
                .build();
    }
}