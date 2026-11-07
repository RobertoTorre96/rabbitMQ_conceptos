package org.example.springboot_rabbitmq.rpc;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RpcProductor {

    private final RabbitTemplate rabbitTemplate;

    public RpcProductor(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping("/rpc")
    public String enviarPeticionSincrona(@RequestParam String operacion) {
        System.out.println(" ⏳ [Cliente HTTP] Enviando petición y BLOQUEANDO el hilo a la espera de respuesta...");

        // convertSendAndReceive: Envía el mensaje y congela la ejecución hasta tener el retorno
        Object respuesta = rabbitTemplate.convertSendAndReceive(
                RpcConfig.EXCHANGE_RPC,
                RpcConfig.ROUTING_RPC,
                operacion
        );

        System.out.println(" ✅ [Cliente HTTP] ¡Respuesta recibida desde RabbitMQ!: " + respuesta);

        // Esta respuesta se imprime directamente en el navegador web del usuario
        return "El servidor procesó tu operación. Resultado: " + respuesta;
    }
}