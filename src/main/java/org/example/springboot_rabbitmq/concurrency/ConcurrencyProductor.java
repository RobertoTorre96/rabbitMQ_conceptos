package org.example.springboot_rabbitmq.concurrency;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConcurrencyProductor {

    private final RabbitTemplate rabbitTemplate;

    public ConcurrencyProductor(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping("/concurrency")
    public String enviarAvalancha() {
        System.out.println(" 🚀 Disparando 15 tareas pesadas de golpe...");
        for (int i = 1; i <= 15; i++) {
            rabbitTemplate.convertAndSend(ConcurrencyConfig.EXCHANGE_CONCURRENCY, ConcurrencyConfig.ROUTING_CONCURRENCY, "Tarea " + i);
        }
        return "Avalancha enviada. Revisa los hilos en tu consola.";
    }
}