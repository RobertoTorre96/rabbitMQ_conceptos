package org.example.springboot_rabbitmq.json;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class JsonConsumidores {

    // Usamos AUTO para mantener el código limpio y manejar los ACKs automáticamente
    @RabbitListener(queues = JsonConfig.QUEUE_JSON, ackMode = "AUTO")
    public void procesarPedido(Pedido pedido) {
        System.out.println(" 📦 [Almacén] Nuevo pedido recibido en formato Objeto: " + pedido.toString());

        // Aquí podrías hacer: pedidoRepository.save(pedido);
        System.out.println(" 📦 [Almacén] Procesando el producto: " + pedido.getProducto());
    }
}