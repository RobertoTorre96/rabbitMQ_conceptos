package org.example.springboot_rabbitmq.idempotencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "mensajes_procesados")
public class MensajeProcesado {

    @Id
    @Column(name = "message_id", length = 100)
    private String messageId;           // PRIMARY KEY: la base rechaza los duplicados

    @Column(name = "procesado_en", nullable = false)
    private LocalDateTime procesadoEn;

    protected MensajeProcesado() { }

    public MensajeProcesado(String messageId) {
        this.messageId = messageId;
        this.procesadoEn = LocalDateTime.now();
    }

    public String getMessageId() { return messageId; }
    public LocalDateTime getProcesadoEn() { return procesadoEn; }
}