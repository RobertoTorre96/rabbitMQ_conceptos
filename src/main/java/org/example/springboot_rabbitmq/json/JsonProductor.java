package org.example.springboot_rabbitmq.json;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class JsonProductor {

    private final RabbitTemplate rabbitTemplate;

    public JsonProductor(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping("/json")
    public String enviarPedidoJson(@RequestParam String producto, @RequestParam double precio) {
        // 1. Creamos un objeto de negocio real
        int idAleatorio = new Random().nextInt(1000);
        Pedido nuevoPedido = new Pedido(idAleatorio, producto, precio);

        // 2. Le pasamos el objeto directamente a RabbitMQ
        // Spring Boot y Jackson lo convertirán a {"id": 123, "producto": "Laptop", "precio": 1500.0}
        rabbitTemplate.convertAndSend(JsonConfig.EXCHANGE_JSON, JsonConfig.ROUTING_JSON, nuevoPedido);

        return "Objeto Pedido enviado a RabbitMQ. Revisa la consola.";
    }
}