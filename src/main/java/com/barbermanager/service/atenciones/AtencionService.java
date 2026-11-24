package com.barbermanager.service.atenciones;

import com.barbermanager.model.Atencion;
import com.barbermanager.model.Cita;
import com.barbermanager.service.Transacciones;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class AtencionService {
    private final Transacciones transacciones;
    public AtencionService(EntityManagerFactory factory) { transacciones = new Transacciones(factory); }

    public Long iniciar(Long citaId) {
        return transacciones.ejecutar(em -> {
            Cita cita = em.find(Cita.class, citaId, LockModeType.PESSIMISTIC_WRITE);
            if (cita == null || !"CONFIRMADA".equals(cita.getEstado()))
                throw new IllegalStateException("La cita debe estar confirmada");
            Atencion atencion = new Atencion();
            atencion.setCita(cita);
            atencion.setInicioReal(LocalDateTime.now(ZoneId.of("America/Lima")));
            cita.setEstado("EN_ATENCION");
            em.persist(atencion);
            em.flush();
            return atencion.getId();
        });
    }

    public void finalizar(Long citaId, String observaciones) {
        transacciones.ejecutar(em -> {
            Cita cita = em.find(Cita.class, citaId, LockModeType.PESSIMISTIC_WRITE);
            if (cita == null || !"EN_ATENCION".equals(cita.getEstado()))
                throw new IllegalStateException("La cita no está en atención");
            Atencion atencion = new com.barbermanager.repository.atenciones.AtencionRepository(em)
                    .buscarPorCita(citaId).orElseThrow(() -> new IllegalStateException("Atención no encontrada"));
            LocalDateTime fin = LocalDateTime.now(ZoneId.of("America/Lima"));
            if (!fin.isAfter(atencion.getInicioReal())) throw new IllegalStateException("La hora de fin debe ser posterior al inicio");
            atencion.setFinReal(fin);
            atencion.setObservaciones(observaciones);
            cita.setEstado("FINALIZADA");
            return null;
        });
    }
}
