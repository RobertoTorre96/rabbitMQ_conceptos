package org.example.springboot_rabbitmq.headers;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class HeadersConfig {

    public static final String EXCHANGE_HEADERS = "exchange_headers";
    public static final String QUEUE_REPORTES = "queue_reportes";

    @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange(EXCHANGE_HEADERS);
    }

    @Bean
    public Queue reportesQueue() {
        return new Queue(QUEUE_REPORTES, true);
    }

    @Bean
    public Binding bindingHeaders(Queue reportesQueue, HeadersExchange headersExchange) {
        // Creamos las reglas del filtro
        Map<String, Object> reglas = new HashMap<>();
        reglas.put("formato", "pdf");
        reglas.put("departamento", "ventas");

        // whereAll = Debe cumplir TODAS las reglas (AND lógico)
        // whereAny = Debe cumplir AL MENOS UNA regla (OR lógico)
        return BindingBuilder.bind(reportesQueue)
                .to(headersExchange)
                .whereAll(reglas).match();
    }
}