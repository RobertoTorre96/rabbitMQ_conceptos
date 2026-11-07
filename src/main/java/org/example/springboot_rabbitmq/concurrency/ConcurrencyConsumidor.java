package org.example.springboot_rabbitmq.concurrency;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ConcurrencyConsumidor {

    // concurrency = "min-max" (Mínimo de hilos - Máximo de hilos)
    @RabbitListener(queues = ConcurrencyConfig.QUEUE_CONCURRENCY, concurrency = "3-5", ackMode = "AUTO")
    public void trabajadorClonado(String tarea) throws InterruptedException {
        String nombreHilo = Thread.currentThread().getName();
        System.out.println(" ⚙️ [" + nombreHilo + "] Iniciando: " + tarea);

        // Simulamos que la tarea es pesada y tarda 2 segundos
        Thread.sleep(2000);

        System.out.println(" ✅ [" + nombreHilo + "] Terminó: " + tarea);
    }
}