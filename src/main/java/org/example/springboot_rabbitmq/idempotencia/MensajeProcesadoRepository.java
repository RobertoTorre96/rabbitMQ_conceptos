package org.example.springboot_rabbitmq.idempotencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MensajeProcesadoRepository extends JpaRepository<MensajeProcesado, String> {

    // Devuelve 1 si inserto el ID (mensaje nuevo) y 0 si ya existia (duplicado)
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "INSERT INTO mensajes_procesados (message_id, procesado_en) "
            + "SELECT CAST(:id AS VARCHAR(100)), CURRENT_TIMESTAMP "
            + "WHERE NOT EXISTS (SELECT 1 FROM mensajes_procesados WHERE message_id = :id)",
            nativeQuery = true)
    int insertarSiNoExiste(@Param("id") String id);

    List<MensajeProcesado> findAllByOrderByProcesadoEnAsc();
}