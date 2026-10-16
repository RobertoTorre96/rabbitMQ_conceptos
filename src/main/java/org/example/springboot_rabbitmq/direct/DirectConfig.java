package org.example.springboot_rabbitmq.direct;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DirectConfig {

    public static final String EXCHANGE_NAME = "mi_exchange_direct";
    public static final String QUEUE_INFO = "queue_direct_info";
    public static final String QUEUE_ERROR = "queue_direct_error";

    // 1. Creamos el Exchange tipo Direct (El clasificador estricto)
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    // 2. Creamos las dos colas
    @Bean
    public Queue colaInfo() {
        return new Queue(QUEUE_INFO, true);
    }

    @Bean
    public Queue colaError() {
        return new Queue(QUEUE_ERROR, true);
    }

    // 3. Tiramos los cables ASIGNANDO UN ROUTING KEY ESTRICTO
    @Bean
    public Binding bindingInfo(Queue colaInfo, DirectExchange directExchange) {
        // Solo recibe mensajes cuya etiqueta sea exactamente "info"
        return BindingBuilder.bind(colaInfo).to(directExchange).with("info");
    }

    @Bean
    public Binding bindingError(Queue colaError, DirectExchange directExchange) {
        // Solo recibe mensajes cuya etiqueta sea exactamente "error"
        return BindingBuilder.bind(colaError).to(directExchange).with("error");
    }
}