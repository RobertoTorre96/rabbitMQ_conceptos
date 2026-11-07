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

    private void estado() {
        System.out.println("[ESTADO] saldo=" + saldoService.getSaldo()
                + " | IDs registrados=" + saldoService.getProcesados());
    }

    // Sin ackMode: usa el modo MANUAL del application.yaml
    @RabbitListener(queues = IdempotenciaConfig.QUEUE_PAGOS)
    public void procesar(PagoEvento evento,
                         Channel channel,
                         @Header(AmqpHeaders.DELIVERY_TAG) long tag,
                         @Header(AmqpHeaders.REDELIVERED) boolean redelivered,
                         @Header(AmqpHeaders.MESSAGE_ID) String messageId) throws IOException {

        String modo = saldoService.getModo();
        System.out.println("==================================================");
        System.out.println("[CONSUMER] Mensaje " + messageId + " | monto=" + evento.monto()
                + " | redelivered=" + redelivered + " | modo=" + modo);

        boolean fallar = evento.fallarEnMedio() && !redelivered;   // falla solo la 1a vez
        boolean aplicado;
        try {
            aplicado = switch (modo) {
                case "sin-tx" -> saldoService.sumarUnaVezSinTx(messageId, evento.monto(), fallar);
                case "con-tx" -> saldoService.sumarUnaVezConTx(messageId, evento.monto(), fallar);
                default -> {
                    System.out.println("      [DB] (modo ingenuo: no se consulta ni se registra el ID)");
                    saldoService.sumarIngenuo(evento.monto());
                    yield true;
                }
            };
        } catch (Exception e) {
            System.out.println("[CONSUMER] ERROR: " + e.getMessage());
            System.out.println("[CONSUMER] RESULTADO: NACK con requeue -> RabbitMQ lo REENTREGARA");
            estado();
            channel.basicNack(tag, false, true);
            return;
        }

        if (!aplicado) {
            System.out.println("[CONSUMER] RESULTADO: DUPLICADO IGNORADO (se hace ACK igual, el efecto ya estaba)");
            if (redelivered && evento.fallarEnMedio() && modo.equals("sin-tx")) {
                System.out.println("[CONSUMER] !!! PAGO PERDIDO !!! El ID se registro en el intento que fallo,");
                System.out.println("[CONSUMER] !!! pero el saldo nunca se sumo. Sin transaccion no hay forma de saberlo.");
            }
            estado();
            channel.basicAck(tag, false);
            return;
        }

        // Simula caida DESPUES de aplicar y ANTES del ACK
        if (evento.simularCaida() && !redelivered) {
            System.out.println("[CONSUMER] RESULTADO: APLICADO, pero SIMULO CAIDA antes del ACK");
            System.out.println("[CONSUMER] -> NACK con requeue: RabbitMQ lo REENTREGARA (duplicado en camino)");
            estado();
            channel.basicNack(tag, false, true);
            return;
        }

        System.out.println("[CONSUMER] RESULTADO: APLICADO y ACK enviado");
        estado();
        channel.basicAck(tag, false);
    }
}