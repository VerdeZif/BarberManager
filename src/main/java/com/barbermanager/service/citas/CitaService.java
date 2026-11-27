package com.barbermanager.service.citas;

import com.barbermanager.model.*;
import com.barbermanager.repository.barberos.BarberoRepository;
import com.barbermanager.repository.citas.CitaRepository;
import com.barbermanager.service.Transacciones;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;

public class CitaService {
    private final Transacciones transacciones;
    public CitaService(EntityManagerFactory factory) { this.transacciones = new Transacciones(factory); }

    /** El controlador debe comprobar la identidad y los permisos del solicitante. */
    public Long reservar(Long clienteId, Long barberoId, Long servicioId, LocalDateTime inicio) {
        return transacciones.ejecutar(em -> {
            Barbero barbero = new BarberoRepository(em).buscarPorIdConBloqueo(barberoId)
                    .orElseThrow(() -> new IllegalArgumentException("Barbero no encontrado"));
            Cliente cliente = obtener(em, Cliente.class, clienteId);
            Servicio servicio = obtener(em, Servicio.class, servicioId);
            if (!cliente.isActivo() || !barbero.isActivo() || !servicio.isActivo()) {
                throw new IllegalArgumentException("Cliente, barbero o servicio inactivo");
            }
            LocalDateTime fin = validarHorario(em, barbero, servicio.getDuracionMinutos(), inicio, null);
            Cita cita = new Cita();
            cita.setCliente(cliente);
            cita.setBarbero(barbero);
            cita.setServicio(servicio);
            cita.setInicio(inicio);
            cita.setFin(fin);
            cita.setPrecioReservado(servicio.getPrecio());
            cita.setEstado("PENDIENTE");
            em.persist(cita);
            em.flush();
            return cita.getId();
        });
    }

    public void cambiarEstado(Long citaId, String nuevoEstado) {
        transacciones.ejecutar(em -> {
            Cita cita = em.find(Cita.class, citaId, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
            if (cita == null) throw new IllegalArgumentException("Cita no encontrada");
            String actual = cita.getEstado();
            boolean permitido = ("PENDIENTE".equals(actual) && ("CONFIRMADA".equals(nuevoEstado) || "CANCELADA".equals(nuevoEstado)))
                    || ("CONFIRMADA".equals(actual) && ("CANCELADA".equals(nuevoEstado) || "NO_ASISTIO".equals(nuevoEstado)));
            if (!permitido) throw new IllegalStateException("Transición de cita no permitida");
            cita.setEstado(nuevoEstado);
            return null;
        });
    }

    /** Conserva el barbero y la duración histórica de la cita. */
    public void reprogramar(Long citaId, LocalDateTime inicio) {
        transacciones.ejecutar(em -> {
            Cita cita = em.find(Cita.class, citaId);
            if (cita == null) throw new IllegalArgumentException("Cita no encontrada");
            Long barberoId = cita.getBarbero().getId();
            Barbero barbero = new BarberoRepository(em).buscarPorIdConBloqueo(barberoId)
                    .orElseThrow(() -> new IllegalArgumentException("Barbero no encontrado"));
            em.refresh(cita, jakarta.persistence.LockModeType.PESSIMISTIC_WRITE);
            if (!"PENDIENTE".equals(cita.getEstado()) && !"CONFIRMADA".equals(cita.getEstado()))
                throw new IllegalStateException("La cita no puede reprogramarse");
            if (!barbero.isActivo()) throw new IllegalStateException("Barbero inactivo");
            long minutos = java.time.Duration.between(cita.getInicio(), cita.getFin()).toMinutes();
            LocalDateTime fin = validarHorario(em, barbero, Math.toIntExact(minutos), inicio, citaId);
            cita.setInicio(inicio);
            cita.setFin(fin);
            return null;
        });
    }


    /**
     * Cancela una cita verificando que pertenezca
     * al cliente autenticado.
     */
    public void cancelarPorCliente(
            Long citaId,
            Long usuarioId
    ) {

        if (citaId == null || usuarioId == null) {
            throw new IllegalArgumentException(
                    "Datos de cancelación inválidos."
            );
        }

        transacciones.ejecutar(em -> {

            Cita cita = em.find(
                    Cita.class,
                    citaId,
                    jakarta.persistence.LockModeType.PESSIMISTIC_WRITE
            );

            if (cita == null) {
                throw new IllegalArgumentException(
                        "Cita no encontrada."
                );
            }

            Cliente cliente = cita.getCliente();

            if (cliente.getUsuario() == null ||
                    !usuarioId.equals(
                            cliente.getUsuario().getId()
                    )) {

                throw new SecurityException(
                        "No puedes cancelar una cita ajena."
                );
            }

            String estado = cita.getEstado();

            if (!"PENDIENTE".equals(estado) &&
                    !"CONFIRMADA".equals(estado)) {

                throw new IllegalStateException(
                        "Esta cita ya no puede cancelarse."
                );
            }

            cita.setEstado("CANCELADA");

            return null;
        });
    }


    private LocalDateTime validarHorario(EntityManager em, Barbero barbero, int duracion, LocalDateTime inicio, Long excluirId) {
        if (inicio == null || !inicio.isAfter(LocalDateTime.now(ZoneId.of("America/Lima"))) || duracion <= 0)
            throw new IllegalArgumentException("Horario inválido");
        LocalDateTime fin = inicio.plusMinutes(duracion);
        String dia = Integer.toString(inicio.getDayOfWeek().getValue());
        boolean trabaja = Arrays.stream(barbero.getDiasLaborables().split(",")).map(String::trim).anyMatch(dia::equals);
        if (!trabaja || !inicio.toLocalDate().equals(fin.toLocalDate())
                || inicio.toLocalTime().isBefore(barbero.getHoraInicio()) || fin.toLocalTime().isAfter(barbero.getHoraFin()))
            throw new IllegalArgumentException("Horario fuera de la jornada del barbero");
        boolean ocupado = new CitaRepository(em).buscarSolapadas(barbero.getId(), inicio, fin).stream()
                .anyMatch(c -> !c.getId().equals(excluirId));
        if (ocupado) throw new IllegalStateException("Horario ocupado");
        return fin;
    }

    private static <T> T obtener(EntityManager em, Class<T> tipo, Long id) {
        T entidad = em.find(tipo, id);
        if (entidad == null) throw new IllegalArgumentException("Registro no encontrado: " + tipo.getSimpleName());
        return entidad;
    }
}
