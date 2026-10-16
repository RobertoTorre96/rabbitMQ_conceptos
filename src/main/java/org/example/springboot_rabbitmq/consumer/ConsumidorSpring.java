package org.example.springboot_rabbitmq.consumer;

import com.rabbitmq.client.Channel;
import org.example.springboot_rabbitmq.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class ConsumidorSpring {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void recibirMensaje(String mensaje,
                               Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {

        System.out.println(" [Consumidor Spring] Mensaje recibido: '" + mensaje + "'");

        try {
            channel.basicAck(deliveryTag, false);
            System.out.println(" [v] ACK manual enviado a RabbitMQ");
        } catch (Exception e) {
            System.err.println(" [X] Error procesando");
            try {
                channel.basicNack(deliveryTag, false, true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}