
package com.barbermanager.controller.barberos;

import com.barbermanager.model.Barbero;
import com.barbermanager.model.Cita;
import com.barbermanager.service.atenciones.AtencionService;
import com.barbermanager.util.Jpa;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/barbero/atenciones")
public class BarberoAtencionesServlet extends HttpServlet {

    private static final String VISTA =
            "/WEB-INF/views/panel/atenciones.jsp";

    private static final ZoneId ZONA =
            ZoneId.of("America/Lima");

    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm");

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private boolean validarAcceso(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        if (!"BARBERO".equals(
                request.getAttribute("rolActual"))) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN
            );
            return false;
        }

        HttpSession sesion = request.getSession(false);

        if (sesion == null ||
                !(sesion.getAttribute("usuarioId")
                        instanceof Long)) {

            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return false;
        }

        return true;
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        if (!validarAcceso(request, response)) {
            return;
        }

        Long usuarioId = (Long)
                request.getSession(false)
                        .getAttribute("usuarioId");

        LocalDate fecha;

        try {
            String valor = request.getParameter("fecha");

            fecha = valor == null || valor.isBlank()
                    ? LocalDate.now(ZONA)
                    : LocalDate.parse(valor);

        } catch (DateTimeException e) {
            fecha = LocalDate.now(ZONA);

            request.setAttribute(
                    "mensajeError",
                    "La fecha seleccionada no es válida."
            );
        }

        request.setAttribute(
                "fechaFiltro",
                fecha.toString()
        );

        request.setAttribute(
                "fechaTexto",
                fecha.format(FORMATO_FECHA)
        );

        EntityManager em = null;

        try {
            em = Jpa.factory().createEntityManager();

            List<Barbero> barberos = em.createQuery(
                            "SELECT b FROM Barbero b " +
                                    "WHERE b.usuario.id = :usuarioId",
                            Barbero.class
                    )
                    .setParameter("usuarioId", usuarioId)
                    .setMaxResults(1)
                    .getResultList();

            if (barberos.isEmpty()) {

                request.setAttribute(
                        "mensajeInfo",
                        "Tu usuario no está vinculado " +
                                "a una ficha de barbero."
                );

                request.setAttribute(
                        "atenciones",
                        List.of()
                );

            } else {

                Barbero barbero = barberos.get(0);

                request.setAttribute(
                        "nombreBarbero",
                        barbero.getNombres() + " " +
                                barbero.getApellidos()
                );

                List<Cita> citas = em.createQuery(
                                "SELECT c FROM Cita c " +
                                        "JOIN FETCH c.cliente " +
                                        "JOIN FETCH c.servicio " +
                                        "WHERE c.barbero.id = :barberoId " +
                                        "AND c.inicio >= :desde " +
                                        "AND c.inicio < :hasta " +
                                        "ORDER BY c.inicio",
                                Cita.class
                        )
                        .setParameter("barberoId", barbero.getId())
                        .setParameter("desde", fecha.atStartOfDay())
                        .setParameter(
                                "hasta",
                                fecha.plusDays(1).atStartOfDay()
                        )
                        .getResultList();

                List<Map<String, Object>> filas =
                        new ArrayList<>();

                int confirmadas = 0;
                int enAtencion = 0;
                int finalizadas = 0;

                for (Cita cita : citas) {

                    Map<String, Object> fila =
                            new HashMap<>();

                    fila.put("id", cita.getId());

                    fila.put(
                            "cliente",
                            cita.getCliente().getNombres() +
                                    " " +
                                    cita.getCliente().getApellidos()
                    );

                    fila.put(
                            "servicio",
                            cita.getServicio().getNombre()
                    );

                    fila.put(
                            "inicio",
                            cita.getInicio().format(
                                    FORMATO_HORA
                            )
                    );

                    fila.put(
                            "fin",
                            cita.getFin().format(
                                    FORMATO_HORA
                            )
                    );

                    fila.put("estado", cita.getEstado());
                    fila.put("precio", cita.getPrecioReservado());

                    filas.add(fila);

                    switch (cita.getEstado()) {
                        case "CONFIRMADA" -> confirmadas++;
                        case "EN_ATENCION" -> enAtencion++;
                        case "FINALIZADA" -> finalizadas++;
                        default -> {
                            // Otros estados no suman
                            // a los indicadores.
                        }
                    }
                }

                request.setAttribute(
                        "atenciones",
                        filas
                );

                request.setAttribute(
                        "totalCitas",
                        filas.size()
                );

                request.setAttribute(
                        "confirmadas",
                        confirmadas
                );

                request.setAttribute(
                        "enAtencion",
                        enAtencion
                );

                request.setAttribute(
                        "finalizadas",
                        finalizadas
                );
            }

        } catch (RuntimeException e) {

            throw new ServletException(
                    "No se pudieron cargar las atenciones.",
                    e
            );

        } finally {

            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        request.getRequestDispatcher(VISTA)
                .forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        if (!validarAcceso(request, response)) {
            return;
        }

        request.setCharacterEncoding("UTF-8");

        Long usuarioId = (Long)
                request.getSession(false)
                        .getAttribute("usuarioId");

        String fechaFiltro = LocalDate.now(ZONA).toString();

        try {
            String fecha = request.getParameter("fecha");

            if (fecha != null && !fecha.isBlank()) {
                fechaFiltro = LocalDate.parse(fecha).toString();
            }

            long citaId = Long.parseLong(
                    request.getParameter("citaId")
            );

            if (citaId <= 0) {
                throw new IllegalArgumentException(
                        "Cita inválida."
                );
            }

            String accion =
                    request.getParameter("accion");

            AtencionService servicio =
                    new AtencionService(Jpa.factory());

            if ("iniciar".equals(accion)) {

                servicio.iniciarPorBarbero(
                        citaId,
                        usuarioId
                );

                request.getSession().setAttribute(
                        "flashExito",
                        "Atención iniciada correctamente."
                );

            } else if ("finalizar".equals(accion)) {

                String observaciones =
                        request.getParameter("observaciones");

                if (observaciones != null &&
                        observaciones.length() > 500) {

                    throw new IllegalArgumentException(
                            "Máximo 500 caracteres."
                    );
                }

                servicio.finalizarPorBarbero(
                        citaId,
                        usuarioId,
                        observaciones
                );

                request.getSession().setAttribute(
                        "flashExito",
                        "Atención finalizada correctamente."
                );

            } else {
                throw new IllegalArgumentException(
                        "Operación no permitida."
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
                    "No se pudo procesar la atención.",
                    e
            );
        }

        response.sendRedirect(
                request.getContextPath() +
                        "/barbero/atenciones?fecha=" +
                        fechaFiltro
        );
    }
}
