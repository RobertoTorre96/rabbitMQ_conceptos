package org.example.springboot_rabbitmq.qos;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QosProductor {

    private final RabbitTemplate rabbitTemplate;

    public QosProductor(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping("/qos")
    public String enviarRafaga() {
        System.out.println(" 🚀 Disparando ráfaga de 10 mensajes...");
        for (int i = 1; i <= 10; i++) {
            rabbitTemplate.convertAndSend(QosConfig.EXCHANGE_QOS, QosConfig.ROUTING_QOS, "Tarea " + i);
        }
        return "Ráfaga de 10 tareas enviada de golpe";
    }
}