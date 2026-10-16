package org.example.springboot_rabbitmq.qos;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QosConfig {

    public static final String EXCHANGE_QOS = "exchange_qos";
    public static final String QUEUE_QOS = "queue_qos";
    public static final String ROUTING_QOS = "rutina.qos";

    @Bean
    public DirectExchange qosExchange() { return new DirectExchange(EXCHANGE_QOS); }

    @Bean
    public Queue qosQueue() { return new Queue(QUEUE_QOS, true); }

    @Bean
    public Binding bindingQos(Queue qosQueue, DirectExchange qosExchange) {
        return BindingBuilder.bind(qosQueue).to(qosExchange).with(ROUTING_QOS);
    }
}