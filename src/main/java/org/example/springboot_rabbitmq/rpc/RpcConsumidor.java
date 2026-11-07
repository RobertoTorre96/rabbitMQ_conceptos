package org.example.springboot_rabbitmq.rpc;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RpcConsumidor {

    // IMPORTANTE: El método retorna un String en lugar de ser 'void'
    @RabbitListener(queues = RpcConfig.QUEUE_RPC, ackMode = "AUTO")
    public String procesarCalculoComplejo(String operacion) throws InterruptedException {
        System.out.println(" 🧮 [Servidor RPC] Recibida petición de cálculo: " + operacion);

        // Simulamos un procesamiento pesado (ej. Inteligencia Artificial, OCR, motor de reglas)
        Thread.sleep(3000);

        String resultado = "RESULTADO_PROCESADO_DE_[" + operacion + "]";
        System.out.println(" 🧮 [Servidor RPC] Cálculo terminado. Devolviendo respuesta...");

        return resultado;
    }
}