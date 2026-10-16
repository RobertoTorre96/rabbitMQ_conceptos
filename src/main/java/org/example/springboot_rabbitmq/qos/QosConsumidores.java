package org.example.springboot_rabbitmq.qos;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class QosConsumidores {

    @RabbitListener(queues = QosConfig.QUEUE_QOS)
    public void trabajadorLento(String mensaje, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws Exception {
        System.out.println(" 🐢 [Lento] Recibió: " + mensaje + " (Procesando...)");
        Thread.sleep(2000); // Finge que tarda 2 segundos
        channel.basicAck(tag, false);
        System.out.println(" 🐢 [Lento] Terminó: " + mensaje);
    }

    @RabbitListener(queues = QosConfig.QUEUE_QOS)
    public void trabajadorRapido(String mensaje, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws Exception {
        System.out.println(" 🐇 [Rápido] Recibió: " + mensaje + " (Procesando...)");
        Thread.sleep(500); // Finge que tarda medio segundo
        channel.basicAck(tag, false);
        System.out.println(" 🐇 [Rápido] Terminó: " + mensaje);
    }
}