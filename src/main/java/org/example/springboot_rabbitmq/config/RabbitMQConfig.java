package org.example.springboot_rabbitmq.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "queue_spring_basico";

    @Bean
    public Queue miColaBasica() {
        return new Queue(QUEUE_NAME, true);
    }
}