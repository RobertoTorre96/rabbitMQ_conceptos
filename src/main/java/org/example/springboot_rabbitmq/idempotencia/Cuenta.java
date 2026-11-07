package org.example.springboot_rabbitmq.idempotencia;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cuenta")
public class Cuenta {

    @Id
    private Integer id;
    private int saldo;

    protected Cuenta() { }              // JPA lo exige

    public Cuenta(Integer id, int saldo) {
        this.id = id;
        this.saldo = saldo;
    }

    public Integer getId() { return id; }
    public int getSaldo() { return saldo; }
}