package org.example.springboot_rabbitmq.retry;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RetryConsumidores {

    // 1. Anulamos el modo manual del YAML solo para este método con ackMode = "AUTO"
    @RabbitListener(queues = RetryConfig.QUEUE_RETRY, ackMode = "AUTO")
    public void procesarConReintentos(String mensaje) throws Exception {
        System.out.println(" 🔄 [Obrero Retry] Procesando: '" + mensaje + "'");

        // 2. Simulamos el fallo
        if (mensaje.toLowerCase().contains("error")) {
            System.out.println(" ⚠️ [X] Fallo detectado. Explotando para que Spring reintente...");
            throw new RuntimeException("Caída temporal de la base de datos");
        }

        // 3. Ya no necesitamos parámetros (Channel/tag) ni channel.basicAck().
        // ¡Spring enviará el ACK automáticamente al terminar esta línea con éxito!
        System.out.println(" ✅ [v] Éxito. Spring enviará el ACK automático.");
    }

    // Aplicamos AUTO también al hospital para que haga la limpieza sola
    @RabbitListener(queues = RetryConfig.QUEUE_RETRY_DLQ, ackMode = "AUTO")
    public void procesarRetryDlq(String mensaje) {
        System.out.println(" 🚑 [Hospital Retry] Mensaje rescatado tras agotar los 3 intentos: '" + mensaje + "'");
    }
}