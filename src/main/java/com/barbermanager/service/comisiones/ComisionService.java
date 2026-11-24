package com.barbermanager.service.comisiones;

import com.barbermanager.model.Comision;
import com.barbermanager.service.Transacciones;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class ComisionService {
    private final Transacciones transacciones;
    public ComisionService(EntityManagerFactory factory) { transacciones = new Transacciones(factory); }
    public void marcarPagada(Long id) {
        transacciones.ejecutar(em -> {
            Comision comision = em.find(Comision.class, id, LockModeType.PESSIMISTIC_WRITE);
            if (comision == null) throw new IllegalArgumentException("Comisión no encontrada");
            if (!"PENDIENTE".equals(comision.getEstado())) throw new IllegalStateException("La comisión no está pendiente");
            comision.setEstado("PAGADA");
            comision.setFechaPago(LocalDateTime.now(ZoneId.of("America/Lima")));
            return null;
        });
    }
}
