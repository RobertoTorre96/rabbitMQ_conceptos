package org.example.springboot_rabbitmq.headers;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class HeadersConsumidor {

    // Extraemos el cuerpo del mensaje (String) y todo el sobre de metadatos (Map)
    @RabbitListener(queues = HeadersConfig.QUEUE_REPORTES, ackMode = "AUTO")
    public void procesarReporte(String reporte, @Headers Map<String, Object> cabeceras) {
        System.out.println(" 🗂️ [Archivo] Recibido: " + reporte);
        System.out.println(" 🏷️ [Cabeceras detectadas]: " + cabeceras);
    }
}