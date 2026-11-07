package org.example.springboot_rabbitmq.idempotencia;

public record PagoEvento(int monto, boolean simularCaida, boolean fallarEnMedio) { }