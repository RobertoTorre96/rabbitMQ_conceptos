package org.example.springboot_rabbitmq.idempotencia;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PagoConsumidor {

    private final SaldoService saldoService;

    public PagoConsumidor(SaldoService saldoService) {
        this.saldoService = saldoService;
    }

    // Sin ackMode: usa el modo MANUAL del application.yaml
    @RabbitListener(queues = IdempotenciaConfig.QUEUE_PAGOS)
    public void procesar(PagoEvento evento,
                         Channel channel,
                         @Header(AmqpHeaders.DELIVERY_TAG) long tag,
                         @Header(AmqpHeaders.REDELIVERED) boolean redelivered,
                         @Header(AmqpHeaders.MESSAGE_ID) String messageId) throws IOException {

        String modo = saldoService.getModo();
        System.out.println(" [Pagos] Recibido " + messageId + " monto=" + evento.monto()
                + " (redelivered=" + redelivered + ") modo=" + modo);

        boolean fallar = evento.fallarEnMedio() && !redelivered;   // falla solo la 1a vez
        boolean aplicado;
        try {
            aplicado = switch (modo) {
                case "sin-tx" -> saldoService.sumarUnaVezSinTx(messageId, evento.monto(), fallar);
                case "con-tx" -> saldoService.sumarUnaVezConTx(messageId, evento.monto(), fallar);
                default -> {
                    saldoService.sumarIngenuo(evento.monto());
                    yield true;
                }
            };
        } catch (Exception e) {
            System.out.println(" [Pagos] ERROR: " + e.getMessage() + " -> NACK con requeue");
            channel.basicNack(tag, false, true);
            return;
        }

        if (!aplicado) {
            System.out.println(" [Pagos] Duplicado IGNORADO: " + messageId);
            channel.basicAck(tag, false);   // se confirma igual: el efecto ya estaba aplicado
            return;
        }
        System.out.println(" [Pagos] Aplicado. Saldo actual: " + saldoService.getSaldo());

        // Simula caida DESPUES de aplicar y ANTES del ACK
        if (evento.simularCaida() && !redelivered) {
            System.out.println(" [Pagos] Simulo caida ANTES del ACK");
            channel.basicNack(tag, false, true);
            return;
        }
        channel.basicAck(tag, false);
    }
}