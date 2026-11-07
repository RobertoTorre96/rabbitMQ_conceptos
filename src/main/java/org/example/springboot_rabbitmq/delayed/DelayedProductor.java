package org.example.springboot_rabbitmq.delayed;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalTime;

@RestController
public class DelayedProductor {

    private final RabbitTemplate rabbitTemplate;

    public DelayedProductor(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping("/delayed")
    public String enviarAlFuturo(@RequestParam int segundos, @RequestParam String texto) {
        System.out.println(" ⏳ [Productor] Disparando a las " + LocalTime.now() + ". Despertará en " + segundos + " seg.");

        // Enviamos directamente a la sala de espera (usando su nombre como routing key genérico)
        rabbitTemplate.convertAndSend(DelayedConfig.QUEUE_ESPERA, (Object) texto, mensaje -> {
            // Transformamos los segundos que llegan por URL a milisegundos (String)
            mensaje.getMessageProperties().setExpiration(String.valueOf(segundos * 1000));
            return mensaje;
        });

        return "Mensaje en sala de espera. Revisa la consola.";
    }
}