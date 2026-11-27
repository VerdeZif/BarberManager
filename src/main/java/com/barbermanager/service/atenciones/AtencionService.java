
package com.barbermanager.service.atenciones;

import com.barbermanager.model.Atencion;
import com.barbermanager.model.Barbero;
import com.barbermanager.model.Cita;
import com.barbermanager.service.Transacciones;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class AtencionService {

    private final Transacciones transacciones;

    private static final ZoneId ZONA =
            ZoneId.of("America/Lima");

    public AtencionService(EntityManagerFactory factory) {
        this.transacciones = new Transacciones(factory);
    }

    // Operación existente para Recepción.
    public Long iniciar(Long citaId) {
        return iniciarInterno(citaId, null);
    }

    // Operación exclusiva del barbero autenticado.
    public Long iniciarPorBarbero(
            Long citaId,
            Long usuarioId
    ) {
        if (usuarioId == null) {
            throw new SecurityException(
                    "Debes iniciar sesión."
            );
        }

        return iniciarInterno(citaId, usuarioId);
    }

    private Long iniciarInterno(
            Long citaId,
            Long usuarioBarberoId
    ) {
        return transacciones.ejecutar(em -> {

            Cita cita = em.find(
                    Cita.class,
                    citaId,
                    LockModeType.PESSIMISTIC_WRITE
            );

            if (cita == null) {
                throw new IllegalArgumentException(
                        "Cita no encontrada."
                );
            }

            validarPropietario(cita, usuarioBarberoId);

            if (!"CONFIRMADA".equals(cita.getEstado())) {
                throw new IllegalStateException(
                        "La cita debe estar confirmada."
                );
            }

            Atencion atencion = new Atencion();

            atencion.setCita(cita);
            atencion.setInicioReal(
                    LocalDateTime.now(ZONA)
            );

            cita.setEstado("EN_ATENCION");

            em.persist(atencion);
            em.flush();

            return atencion.getId();
        });
    }

    // Operación existente para Recepción.
    public void finalizar(
            Long citaId,
            String observaciones
    ) {
        finalizarInterno(citaId, null, observaciones);
    }

    // Operación exclusiva del barbero autenticado.
    public void finalizarPorBarbero(
            Long citaId,
            Long usuarioId,
            String observaciones
    ) {
        if (usuarioId == null) {
            throw new SecurityException(
                    "Debes iniciar sesión."
            );
        }

        finalizarInterno(
                citaId,
                usuarioId,
                observaciones
        );
    }

    private void finalizarInterno(
            Long citaId,
            Long usuarioBarberoId,
            String observaciones
    ) {
        if (observaciones != null
                && observaciones.length() > 500) {
            throw new IllegalArgumentException(
                    "Las observaciones no pueden superar " +
                            "los 500 caracteres."
            );
        }

        transacciones.ejecutar(em -> {

            Cita cita = em.find(
                    Cita.class,
                    citaId,
                    LockModeType.PESSIMISTIC_WRITE
            );

            if (cita == null) {
                throw new IllegalArgumentException(
                        "Cita no encontrada."
                );
            }

            validarPropietario(cita, usuarioBarberoId);

            if (!"EN_ATENCION".equals(cita.getEstado())) {
                throw new IllegalStateException(
                        "La cita no está en atención."
                );
            }

            Atencion atencion =
                    new com.barbermanager.repository.atenciones
                            .AtencionRepository(em)
                            .buscarPorCita(citaId)
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Atención no encontrada."
                                    )
                            );

            LocalDateTime fin =
                    LocalDateTime.now(ZONA);

            if (!fin.isAfter(atencion.getInicioReal())) {
                throw new IllegalStateException(
                        "La hora de fin debe ser posterior al inicio."
                );
            }

            atencion.setFinReal(fin);
            atencion.setObservaciones(observaciones);

            cita.setEstado("FINALIZADA");

            return null;
        });
    }

    private void validarPropietario(
            Cita cita,
            Long usuarioBarberoId
    ) {
        // Cuando se llama desde Recepción,
        // el filtro de roles protege su controlador.
        if (usuarioBarberoId == null) {
            return;
        }

        Barbero barbero = cita.getBarbero();

        if (barbero == null ||
                barbero.getUsuario() == null ||
                !usuarioBarberoId.equals(
                        barbero.getUsuario().getId()
                )) {

            throw new SecurityException(
                    "No puedes gestionar una cita " +
                            "asignada a otro barbero."
            );
        }
    }
}
