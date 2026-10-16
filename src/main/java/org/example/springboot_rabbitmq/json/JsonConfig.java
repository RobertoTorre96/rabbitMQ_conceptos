package org.example.springboot_rabbitmq.json;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonConfig {

    public static final String EXCHANGE_JSON = "exchange_json";
    public static final String QUEUE_JSON = "queue_json";
    public static final String ROUTING_JSON = "rutina.json";

    // EL MOTOR DE TRADUCCIÓN: Convierte Objetos <-> JSON
    @Bean
    public MessageConverter conversorJson() {
        return new JacksonJsonMessageConverter();
    }

    // Infraestructura normal
    @Bean
    public DirectExchange jsonExchange() {
        return new DirectExchange(EXCHANGE_JSON);
    }

    @Bean
    public Queue jsonQueue() {
        return new Queue(QUEUE_JSON, true);
    }

    @Bean
    public Binding bindingJson(Queue jsonQueue, DirectExchange jsonExchange) {
        return BindingBuilder.bind(jsonQueue).to(jsonExchange).with(ROUTING_JSON);
    }
}