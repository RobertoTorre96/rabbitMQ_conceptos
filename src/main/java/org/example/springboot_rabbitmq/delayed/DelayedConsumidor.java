package org.example.springboot_rabbitmq.delayed;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.time.LocalTime;

@Component
public class DelayedConsumidor {

    @RabbitListener(queues = DelayedConfig.QUEUE_DESTINO, ackMode = "AUTO")
    public void procesarMensajeFuturo(String texto) {
        System.out.println(" ⏰ [Consumidor] Mensaje recibido a las " + LocalTime.now() + " -> " + texto);
    }
}