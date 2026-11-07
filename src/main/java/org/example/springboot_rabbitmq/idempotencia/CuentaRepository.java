package org.example.springboot_rabbitmq.idempotencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Integer> {

    // UPDATE atomico en la base: saldo = saldo + monto (sin leer y escribir desde Java)
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Cuenta c SET c.saldo = c.saldo + :monto WHERE c.id = :id")
    int sumarSaldo(@Param("id") int id, @Param("monto") int monto);
}