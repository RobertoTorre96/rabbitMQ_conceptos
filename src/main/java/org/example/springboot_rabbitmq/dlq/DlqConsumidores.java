package org.example.springboot_rabbitmq.dlq;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class DlqConsumidores {

    @RabbitListener(queues = DlqConfig.QUEUE_NORMAL)
    public void procesarNormal(String mensaje, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        System.out.println(" 👷 [Obrero Normal] Intentando procesar: '" + mensaje + "'");

        try {
            // Simulamos un "Poison Message" si el texto contiene la palabra "error"
            if (mensaje.toLowerCase().contains("error")) {
                throw new RuntimeException("Excepción en base de datos o formato inválido");
            }

            // Si todo sale bien, mandamos el ACK
            channel.basicAck(tag, false);
            System.out.println(" 👷 [v] Éxito. ACK enviado.");

        } catch (Exception e) {
            System.err.println(" 👷 [X] Falló: " + e.getMessage() + ". Descartando mensaje...");
            try {
                // EL GATILLO: Nack con requeue=false manda el mensaje al x-dead-letter-exchange
                channel.basicNack(tag, false, false);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @RabbitListener(queues = DlqConfig.QUEUE_DLQ)
    public void procesarDlq(String mensaje, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        System.out.println(" 🚑 [Hospital DLQ] Mensaje rescatado de la muerte: '" + mensaje + "'");
        try {
            // Aquí en la vida real notificarías a soporte o guardarías el log
            channel.basicAck(tag, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}