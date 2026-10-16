package org.example.springboot_rabbitmq.confirms;

import jakarta.annotation.PostConstruct;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfirmsProductor {

    private final RabbitTemplate rabbitTemplate;

    public ConfirmsProductor(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // Instalamos las "antenas" una sola vez al arrancar la aplicación
    @PostConstruct
    public void configurarCallbacks() {
        // Antena 1: ¿Llegó al Exchange?
        rabbitTemplate.setConfirmCallback((correlationData, ack, causaError) -> {
            if (ack) {
                System.out.println(" 🟢 [Recibo] ¡Éxito! RabbitMQ guardó el mensaje de forma segura.");
            } else {
                System.err.println(" 🔴 [Recibo] ERROR GRAVE: El mensaje NO llegó al Exchange. Causa: " + causaError);
            }
        });

        // Antena 2: Llegó al Exchange, pero ¿encontró una ruta válida hacia una cola?
        rabbitTemplate.setReturnsCallback(returned -> {
            System.err.println(" 🟡 [Devolución] El Exchange existe, pero no encontró ninguna cola para la ruta: "
                    + returned.getRoutingKey());
            System.err.println(" 🟡 Mensaje devuelto: " + new String(returned.getMessage().getBody()));
        });
    }

    @GetMapping("/confirms/exito")
    public String enviarExito(@RequestParam String texto) {
        System.out.println(" 🚀 Enviando mensaje correcto...");
        // Ruta perfecta
        rabbitTemplate.convertAndSend(ConfirmsConfig.EXCHANGE_CONFIRMS, ConfirmsConfig.ROUTING_CONFIRMS, texto);
        return "Enviado al flujo exitoso. Revisa la consola.";
    }

    @GetMapping("/confirms/error-exchange")
    public String enviarErrorExchange(@RequestParam String texto) {
        System.out.println(" 🚀 Enviando a un Exchange FANTASMA...");
        // Simulamos un error de tipeo en el nombre del Exchange
        rabbitTemplate.convertAndSend("exchange_que_no_existe", ConfirmsConfig.ROUTING_CONFIRMS, texto);
        return "Enviado a un exchange falso. Revisa la consola.";
    }
}