package org.example.springboot_rabbitmq.direct;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DirectProductor {

    private final RabbitTemplate rabbitTemplate;

    public DirectProductor(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping("/direct")
    public String enviarDirect(@RequestParam String routingKey, @RequestParam String texto) {

        // AQUÍ ES VITAL EL ROUTING KEY (El segundo parámetro)
        rabbitTemplate.convertAndSend(DirectConfig.EXCHANGE_NAME, routingKey, texto);

        return "Mensaje enviado al Exchange Direct con la etiqueta [" + routingKey + "]: " + texto;
    }
}