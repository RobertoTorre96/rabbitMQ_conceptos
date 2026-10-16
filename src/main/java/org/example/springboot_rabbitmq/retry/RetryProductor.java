package org.example.springboot_rabbitmq.retry;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RetryProductor {

    private final RabbitTemplate rabbitTemplate;

    public RetryProductor(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping("/retry")
    public String enviarRetry(@RequestParam String texto) {
        rabbitTemplate.convertAndSend(RetryConfig.EXCHANGE_RETRY, RetryConfig.ROUTING_RETRY, texto);
        return "Mensaje enviado a la cola de reintentos: " + texto;
    }
}