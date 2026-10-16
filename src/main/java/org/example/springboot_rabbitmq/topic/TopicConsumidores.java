package org.example.springboot_rabbitmq.topic;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class TopicConsumidores {

    @RabbitListener(queues = TopicConfig.QUEUE_USA)
    public void escucharUsa(String mensaje, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        System.out.println(" 🇺🇸 [Noticiero USA] Recibe: '" + mensaje + "'");
        try { channel.basicAck(tag, false); } catch (Exception e) {}
    }

    @RabbitListener(queues = TopicConfig.QUEUE_DEPORTES)
    public void escucharDeportes(String mensaje, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        System.out.println(" ⚽ [Canal Deportes] Recibe: '" + mensaje + "'");
        try { channel.basicAck(tag, false); } catch (Exception e) {}
    }
}