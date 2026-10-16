package org.example.springboot_rabbitmq.topic;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TopicProductor {

    private final RabbitTemplate rabbitTemplate;

    public TopicProductor(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping("/topic")
    public String enviarTopic(@RequestParam String routingKey, @RequestParam String texto) {
        rabbitTemplate.convertAndSend(TopicConfig.EXCHANGE_NAME, routingKey, texto);
        return "Mensaje Topic enviado con ruta [" + routingKey + "]";
    }
}