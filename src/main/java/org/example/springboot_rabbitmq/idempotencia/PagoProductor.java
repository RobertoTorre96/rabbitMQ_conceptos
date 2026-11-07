package org.example.springboot_rabbitmq.idempotencia;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/idempotencia")
public class PagoProductor {

    private final RabbitTemplate rabbitTemplate;
    private final SaldoService saldoService;

    public PagoProductor(RabbitTemplate rabbitTemplate, SaldoService saldoService) {
        this.rabbitTemplate = rabbitTemplate;
        this.saldoService = saldoService;
    }

    @GetMapping("/pagar")
    public String pagar(@RequestParam String messageId,
                        @RequestParam int monto,
                        @RequestParam(defaultValue = "false") boolean simularCaida,
                        @RequestParam(defaultValue = "false") boolean fallarEnMedio) {

        PagoEvento evento = new PagoEvento(monto, simularCaida, fallarEnMedio);

        rabbitTemplate.convertAndSend(
                IdempotenciaConfig.EXCHANGE,
                IdempotenciaConfig.ROUTING_KEY,
                evento,
                mensaje -> {
                    mensaje.getMessageProperties().setMessageId(messageId);   // ID estable
                    return mensaje;
                });

        return "Pago enviado con messageId=" + messageId;
    }

    @GetMapping("/saldo")
    public String saldo() {
        return "Saldo: " + saldoService.getSaldo()
                + " | modo: " + saldoService.getModo()
                + " | IDs registrados: " + saldoService.getProcesados();
    }

    @GetMapping("/modo")
    public String modo(@RequestParam String valor) {
        if (!Set.of("ingenuo", "sin-tx", "con-tx").contains(valor)) {
            return "Valores validos: ingenuo | sin-tx | con-tx";
        }
        saldoService.setModo(valor);
        return "Modo = " + valor;
    }

    @GetMapping("/reset")
    public String reset() {
        saldoService.reset();
        return "Saldo en 0 y tabla de IDs vaciada";
    }
}