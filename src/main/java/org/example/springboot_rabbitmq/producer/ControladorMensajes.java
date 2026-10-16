package org.example.springboot_rabbitmq.producer;

import org.example.springboot_rabbitmq.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControladorMensajes {

    private final RabbitTemplate rabbitTemplate;

    public ControladorMensajes(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping("/enviar")
    public String enviarMensaje(@RequestParam String texto) {

        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, texto);

        return "Mensaje enviado a RabbitMQ: " + texto;
    }
}