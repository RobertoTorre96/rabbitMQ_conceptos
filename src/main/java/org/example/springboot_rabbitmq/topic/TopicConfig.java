package org.example.springboot_rabbitmq.topic;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicConfig {

    public static final String EXCHANGE_NAME = "mi_exchange_topic";
    public static final String QUEUE_USA = "queue_topic_usa";
    public static final String QUEUE_DEPORTES = "queue_topic_deportes";

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue colaUsa() {
        return new Queue(QUEUE_USA, true);
    }

    @Bean
    public Queue colaDeportes() {
        return new Queue(QUEUE_DEPORTES, true);
    }

    // --- LA MAGIA DE LOS COMODINES ---

    @Bean
    public Binding bindingUsa(Queue colaUsa, TopicExchange topicExchange) {
        // "usa.#" -> Atrapa TODO lo que empiece con "usa.", sin importar cuántas palabras sigan.
        return BindingBuilder.bind(colaUsa).to(topicExchange).with("usa.#");
    }

    @Bean
    public Binding bindingDeportes(Queue colaDeportes, TopicExchange topicExchange) {
        // "*.deportes.*" -> Exige EXACTAMENTE 3 palabras, y la del medio debe ser "deportes".
        return BindingBuilder.bind(colaDeportes).to(topicExchange).with("*.deportes.*");
    }
}