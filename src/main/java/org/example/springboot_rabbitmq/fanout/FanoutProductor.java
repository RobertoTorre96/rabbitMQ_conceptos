package org.example.springboot_rabbitmq.fanout;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FanoutProductor {

    private final RabbitTemplate rabbitTemplate;

    public FanoutProductor(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping("/fanout")
    public String enviarFanout(@RequestParam String texto) {

        // En Fanout el Routing Key se ignora, por eso mandamos un String vacío ""
        rabbitTemplate.convertAndSend(FanoutConfig.EXCHANGE_NAME, "", texto);

        return "Mensaje Broadcast enviado al Exchange: " + texto;
    }
}