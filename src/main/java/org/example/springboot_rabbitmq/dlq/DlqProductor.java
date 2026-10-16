package org.example.springboot_rabbitmq.dlq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DlqProductor {

    private final RabbitTemplate rabbitTemplate;

    public DlqProductor(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping("/dlq")
    public String enviarDlq(@RequestParam String texto) {
        // Siempre enviamos al Exchange y Routing Key normales
        rabbitTemplate.convertAndSend(DlqConfig.EXCHANGE_NORMAL, DlqConfig.ROUTING_NORMAL, texto);
        return "Mensaje enviado al flujo normal: " + texto;
    }
}