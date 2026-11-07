package org.example.springboot_rabbitmq.idempotencia;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SaldoService {

    private static final int CUENTA_ID = 1;

    private final CuentaRepository cuentaRepository;
    private final MensajeProcesadoRepository mensajeRepository;
    private volatile String modo = "ingenuo";   // ingenuo | sin-tx | con-tx

    public SaldoService(CuentaRepository cuentaRepository,
                        MensajeProcesadoRepository mensajeRepository) {
        this.cuentaRepository = cuentaRepository;
        this.mensajeRepository = mensajeRepository;
    }

    @PostConstruct
    public void inicializarCuenta() {
        if (!cuentaRepository.existsById(CUENTA_ID)) {
            cuentaRepository.save(new Cuenta(CUENTA_ID, 0));
        }
    }

    private void log(String mensaje) {
        System.out.println("      [DB] " + mensaje);
    }

    // 1) INGENUO: suma siempre, no registra nada
    public void sumarIngenuo(int monto) {
        int antes = getSaldo();
        int filas = cuentaRepository.sumarSaldo(CUENTA_ID, monto);
        if (filas == 0) throw new IllegalStateException("No existe la cuenta " + CUENTA_ID);
        log("UPDATE cuenta: saldo " + antes + " -> " + (antes + monto));
    }

    // 2) SIN TRANSACCION: cada llamada al repository se confirma sola (autocommit).
    //    Orden peligroso: primero registra el ID, despues aplica el efecto.
    public boolean sumarUnaVezSinTx(String messageId, int monto, boolean fallar) {
        if (!registrarId(messageId)) return false;
        if (fallar) {
            log("FALLO SIMULADO entre el INSERT y el UPDATE.");
            log("Modo SIN transaccion: el INSERT del ID YA QUEDO CONFIRMADO. El saldo NO se sumo.");
            throw new RuntimeException("Caida simulada entre INSERT y UPDATE");
        }
        sumarIngenuo(monto);
        return true;
    }

    // 3) CON TRANSACCION: registrar el ID y sumar el saldo son TODO o NADA
    @Transactional
    public boolean sumarUnaVezConTx(String messageId, int monto, boolean fallar) {
        if (!registrarId(messageId)) return false;
        if (fallar) {
            log("FALLO SIMULADO entre el INSERT y el UPDATE.");
            log("Modo CON transaccion: Spring hara ROLLBACK, el INSERT del ID se DESHACE.");
            throw new RuntimeException("Caida simulada entre INSERT y UPDATE");
        }
        sumarIngenuo(monto);
        return true;
    }

    private boolean registrarId(String messageId) {
        int filas = mensajeRepository.insertarSiNoExiste(messageId);
        if (filas == 1) {
            log("INSERT mensajes_procesados('" + messageId + "') -> OK (ID nuevo)");
        } else {
            log("INSERT mensajes_procesados('" + messageId + "') -> RECHAZADO (el ID ya existia = DUPLICADO)");
        }
        return filas == 1;
    }

    public int getSaldo() {
        return cuentaRepository.findById(CUENTA_ID).map(Cuenta::getSaldo).orElse(0);
    }

    public List<String> getProcesados() {
        return mensajeRepository.findAllByOrderByProcesadoEnAsc().stream()
                .map(MensajeProcesado::getMessageId)
                .toList();
    }

    public String getModo() { return modo; }
    public void setModo(String modo) { this.modo = modo; }

    @Transactional
    public void reset() {
        mensajeRepository.deleteAllInBatch();
        cuentaRepository.save(new Cuenta(CUENTA_ID, 0));
    }
}