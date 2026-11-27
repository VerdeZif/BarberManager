
package com.barbermanager.controller.citas;

import com.barbermanager.model.Barbero;
import com.barbermanager.model.Cita;
import com.barbermanager.model.Servicio;
import com.barbermanager.service.citas.CitaService;
import com.barbermanager.util.Jpa;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {
        "/reservas/nueva",
        "/mis-citas"
})
public class ClienteCitasServlet extends HttpServlet {

    private static final String VISTA_RESERVA =
            "/WEB-INF/views/publico/reserva.jsp";

    private static final String VISTA_CITAS =
            "/WEB-INF/views/publico/mis-citas.jsp";

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Long verificarCliente(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        if (!"CLIENTE".equals(
                request.getAttribute("rolActual"))) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN
            );
            return null;
        }

        HttpSession session = request.getSession(false);

        if (session == null ||
                !(session.getAttribute("usuarioId")
                        instanceof Long)) {

            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return null;
        }

        return (Long) session.getAttribute("usuarioId");
    }

    private Long obtenerClienteId(
            EntityManager em,
            Long usuarioId
    ) {

        List<Long> ids = em.createQuery(
                        "SELECT c.id FROM Cliente c " +
                                "WHERE c.usuario.id = :usuarioId",
                        Long.class
                )
                .setParameter("usuarioId", usuarioId)
                .setMaxResults(1)
                .getResultList();

        return ids.isEmpty() ? null : ids.get(0);
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        Long usuarioId = verificarCliente(
                request, response
        );

        if (usuarioId == null) {
            return;
        }

        boolean esReserva = "/reservas/nueva".equals(
                request.getServletPath()
        );

        EntityManager em = null;

        try {
            em = Jpa.factory().createEntityManager();

            Long clienteId = obtenerClienteId(
                    em, usuarioId
            );

            if (clienteId == null) {
                request.setAttribute(
                        "mensajeInfo",
                        "Tu cuenta todavía no tiene una ficha " +
                                "de cliente vinculada."
                );
            }

            if (esReserva) {

                request.setAttribute(
                        "puedeReservar",
                        clienteId != null
                );

                request.setAttribute(
                        "servicios",
                        em.createQuery(
                                "SELECT s FROM Servicio s " +
                                        "WHERE s.activo = true " +
                                        "ORDER BY s.nombre",
                                Servicio.class
                        ).getResultList()
                );

                request.setAttribute(
                        "barberos",
                        em.createQuery(
                                "SELECT b FROM Barbero b " +
                                        "WHERE b.activo = true " +
                                        "ORDER BY b.nombres, b.apellidos",
                                Barbero.class
                        ).getResultList()
                );

                String servicioSeleccionado =
                        request.getParameter("servicioId");

                if (servicioSeleccionado != null) {
                    try {
                        request.setAttribute(
                                "servicioPreseleccionado",
                                Long.valueOf(servicioSeleccionado)
                        );
                    } catch (NumberFormatException ignored) {
                        // Si no existe, el usuario lo seleccionará.
                    }
                }

                String barberoSeleccionado =
                        request.getParameter("barberoId");

                if (barberoSeleccionado != null
                        && !barberoSeleccionado.isBlank()) {

                    try {
                        long id = Long.parseLong(barberoSeleccionado);

                        if (id > 0) {
                            request.setAttribute(
                                    "barberoPreseleccionado", id
                            );
                        }

                    } catch (NumberFormatException ignored) {
                        // El cliente podrá elegir un barbero manualmente.
                    }
                }

            } else {

                List<Map<String, Object>> filas =
                        new ArrayList<>();

                if (clienteId != null) {

                    List<Cita> citas = em.createQuery(
                                    "SELECT c FROM Cita c " +
                                            "JOIN FETCH c.barbero " +
                                            "JOIN FETCH c.servicio " +
                                            "WHERE c.cliente.id = :clienteId " +
                                            "ORDER BY c.inicio DESC",
                                    Cita.class
                            )
                            .setParameter("clienteId", clienteId)
                            .setMaxResults(100)
                            .getResultList();

                    for (Cita cita : citas) {

                        Map<String, Object> fila =
                                new HashMap<>();

                        fila.put("id", cita.getId());

                        fila.put(
                                "servicio",
                                cita.getServicio().getNombre()
                        );

                        fila.put(
                                "barbero",
                                cita.getBarbero().getNombres() +
                                        " " +
                                        cita.getBarbero().getApellidos()
                        );

                        fila.put(
                                "fecha",
                                cita.getInicio().format(FORMATO)
                        );

                        fila.put(
                                "precio",
                                cita.getPrecioReservado()
                        );

                        fila.put("estado", cita.getEstado());

                        fila.put(
                                "cancelable",
                                "PENDIENTE".equals(cita.getEstado())
                                        || "CONFIRMADA".equals(cita.getEstado())
                        );

                        filas.add(fila);
                    }
                }

                request.setAttribute("citas", filas);
            }

        } catch (RuntimeException e) {

            throw new ServletException(
                    "No se pudo cargar el portal del cliente.",
                    e
            );

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        request.getRequestDispatcher(
                esReserva ? VISTA_RESERVA : VISTA_CITAS
        ).forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        Long usuarioId = verificarCliente(
                request, response
        );

        if (usuarioId == null) {
            return;
        }

        request.setCharacterEncoding("UTF-8");

        boolean esReserva = "/reservas/nueva".equals(
                request.getServletPath()
        );

        String destino = esReserva
                ? "/reservas/nueva"
                : "/mis-citas";

        try {

            CitaService servicio =
                    new CitaService(Jpa.factory());

            if (esReserva) {

                Long clienteId;

                EntityManager em =
                        Jpa.factory().createEntityManager();

                try {
                    clienteId = obtenerClienteId(
                            em, usuarioId
                    );
                } finally {
                    em.close();
                }

                if (clienteId == null) {
                    throw new IllegalStateException(
                            "Tu cuenta no tiene una ficha de cliente."
                    );
                }

                Long barberoId = leerId(
                        request, "barberoId"
                );

                Long servicioId = leerId(
                        request, "servicioId"
                );

                LocalDateTime inicio;

                try {
                    inicio = LocalDateTime.parse(
                            request.getParameter("inicio")
                    );
                } catch (RuntimeException e) {
                    throw new IllegalArgumentException(
                            "Selecciona una fecha y hora válidas."
                    );
                }

                servicio.reservar(
                        clienteId,
                        barberoId,
                        servicioId,
                        inicio
                );

                request.getSession().setAttribute(
                        "flashExito",
                        "Reserva registrada. Espera la " +
                                "confirmación de Recepción."
                );

                destino = "/mis-citas";

            } else {

                if (!"cancelar".equals(
                        request.getParameter("accion"))) {

                    throw new IllegalArgumentException(
                            "Acción no permitida."
                    );
                }

                Long citaId = leerId(
                        request, "citaId"
                );

                servicio.cancelarPorCliente(
                        citaId,
                        usuarioId
                );

                request.getSession().setAttribute(
                        "flashExito",
                        "Tu cita fue cancelada correctamente."
                );
            }

        } catch (SecurityException e) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN
            );
            return;

        } catch (IllegalArgumentException |
                 IllegalStateException e) {

            request.getSession().setAttribute(
                    "flashError",
                    e.getMessage()
            );

        } catch (RuntimeException e) {

            throw new ServletException(
                    "No se pudo procesar la solicitud.",
                    e
            );
        }

        response.sendRedirect(
                request.getContextPath() + destino
        );
    }

    private Long leerId(
            HttpServletRequest request,
            String campo
    ) {

        try {
            long id = Long.parseLong(
                    request.getParameter(campo)
            );

            if (id <= 0) {
                throw new NumberFormatException();
            }

            return id;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Selecciona un valor válido para " +
                            campo + "."
            );
        }
    }
}
