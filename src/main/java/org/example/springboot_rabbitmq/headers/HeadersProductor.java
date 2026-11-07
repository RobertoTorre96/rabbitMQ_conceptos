package org.example.springboot_rabbitmq.headers;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HeadersProductor {

    private final RabbitTemplate rabbitTemplate;

    public HeadersProductor(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping("/headers/valido")
    public String enviarValido() {
        System.out.println(" 🚀 [Envío] Lanzando documento que cumple TODAS las reglas (PDF de Ventas)");

        // Fíjate que el routingKey es una cadena vacía ""
        rabbitTemplate.convertAndSend(HeadersConfig.EXCHANGE_HEADERS, "", "Reporte_Mensual", mensaje -> {
            mensaje.getMessageProperties().setHeader("formato", "pdf");
            mensaje.getMessageProperties().setHeader("departamento", "ventas");
            return mensaje;
        });
        return "Mensaje enviado (Debería ser recibido)";
    }

    @GetMapping("/headers/invalido")
    public String enviarInvalido() {
        System.out.println(" 🚀 [Envío] Lanzando documento que falla la regla (EXCEL de Ventas)");

        rabbitTemplate.convertAndSend(HeadersConfig.EXCHANGE_HEADERS, "", "Presupuesto_Anual", mensaje -> {
            mensaje.getMessageProperties().setHeader("formato", "excel"); // <-- ESTO FALLA LA REGLA
            mensaje.getMessageProperties().setHeader("departamento", "ventas");
            return mensaje;
        });
        return "Mensaje enviado (Será ignorado y destruido por el Exchange)";
    }
}