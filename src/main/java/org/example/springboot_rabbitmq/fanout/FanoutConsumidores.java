package org.example.springboot_rabbitmq.fanout;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class FanoutConsumidores {

    @RabbitListener(queues = FanoutConfig.QUEUE_CONTABILIDAD)
    public void escucharContabilidad(String mensaje, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        System.out.println(" 📊 [Dpto. Contabilidad] Registrando gasto: '" + mensaje + "'");
        try {
            channel.basicAck(tag, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = FanoutConfig.QUEUE_MARKETING)
    public void escucharMarketing(String mensaje, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        System.out.println(" 📢 [Dpto. Marketing] Analizando tendencia: '" + mensaje + "'");
        try {
            channel.basicAck(tag, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}