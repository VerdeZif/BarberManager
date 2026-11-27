
package com.barbermanager.controller.comisiones;

import com.barbermanager.model.*;
import com.barbermanager.service.comisiones.ComisionService;
import com.barbermanager.util.Jpa;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@WebServlet(urlPatterns = {
        "/admin/comisiones",
        "/barbero/comisiones"
})
public class ComisionServlet extends HttpServlet {

    private static final String VISTA =
            "/WEB-INF/views/panel/comisiones.jsp";

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        String ruta = request.getServletPath();
        String rol = (String) request.getAttribute("rolActual");

        boolean esAdmin = "/admin/comisiones".equals(ruta);
        boolean esBarbero = "/barbero/comisiones".equals(ruta);

        if ((!esAdmin || !"ADMINISTRADOR".equals(rol))
                && (!esBarbero || !"BARBERO".equals(rol))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        HttpSession session = request.getSession(false);

        if (session == null ||
                !(session.getAttribute("usuarioId") instanceof Long)) {
            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        Long usuarioId = (Long) session.getAttribute("usuarioId");

        request.setAttribute("esAdmin", esAdmin);

        EntityManager em = null;

        try {
            em = Jpa.factory().createEntityManager();

            Long barberoId = null;

            if (esBarbero) {

                List<Long> ids = em.createQuery(
                                "SELECT b.id FROM Barbero b " +
                                        "WHERE b.usuario.id = :usuarioId",
                                Long.class
                        )
                        .setParameter("usuarioId", usuarioId)
                        .setMaxResults(1)
                        .getResultList();

                if (ids.isEmpty()) {
                    request.setAttribute(
                            "mensajeInfo",
                            "Tu cuenta todavía no está vinculada " +
                                    "a una ficha de barbero. Solicita al " +
                                    "administrador que complete la vinculación."
                    );
                } else {
                    barberoId = ids.get(0);
                }
            }

            List<Comision> resultados = List.of();

            if (esAdmin || barberoId != null) {

                String jpql =
                        "SELECT c FROM Comision c " +
                                "JOIN FETCH c.barbero " +
                                "JOIN FETCH c.atencion ";

                if (!esAdmin) {
                    jpql += "WHERE c.barbero.id = :barberoId ";
                }

                jpql +=
                        "ORDER BY c.fechaGeneracion DESC, c.id DESC";

                var consulta = em.createQuery(
                        jpql, Comision.class
                );

                if (!esAdmin) {
                    consulta.setParameter("barberoId", barberoId);
                }

                resultados = consulta
                        .setMaxResults(200)
                        .getResultList();
            }

            List<Map<String, Object>> filas = new ArrayList<>();

            BigDecimal totalPendiente = BigDecimal.ZERO;
            BigDecimal totalPagado = BigDecimal.ZERO;

            for (Comision c : resultados) {

                Barbero b = c.getBarbero();
                Atencion a = c.getAtencion();
                Cita cita = a.getCita();

                Map<String, Object> fila = new HashMap<>();

                fila.put("id", c.getId());

                fila.put(
                        "barbero",
                        b.getNombres() + " " + b.getApellidos()
                );

                fila.put("atencionId", a.getId());

                fila.put(
                        "servicio",
                        cita.getServicio().getNombre()
                );

                fila.put(
                        "cliente",
                        cita.getCliente().getNombres() + " " +
                                cita.getCliente().getApellidos()
                );

                fila.put("base", c.getBaseServicio());
                fila.put("porcentaje", c.getPorcentaje());
                fila.put("monto", c.getMonto());
                fila.put("estado", c.getEstado());

                fila.put(
                        "fechaGeneracion",
                        formatear(c.getFechaGeneracion())
                );

                fila.put(
                        "fechaPago",
                        formatear(c.getFechaPago())
                );

                filas.add(fila);

                if ("PENDIENTE".equals(c.getEstado())) {
                    totalPendiente = totalPendiente.add(
                            c.getMonto()
                    );
                }

                if ("PAGADA".equals(c.getEstado())) {
                    totalPagado = totalPagado.add(
                            c.getMonto()
                    );
                }
            }

            request.setAttribute("comisiones", filas);
            request.setAttribute("totalPendiente", totalPendiente);
            request.setAttribute("totalPagado", totalPagado);
            request.setAttribute("cantidad", filas.size());

        } catch (RuntimeException e) {
            throw new ServletException(
                    "No se pudieron cargar las comisiones.", e
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

        // Únicamente el administrador puede pagar comisiones.
        if (!"/admin/comisiones".equals(request.getServletPath())
                || !"ADMINISTRADOR".equals(
                request.getAttribute("rolActual")
        )) {

            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {

            String accion = request.getParameter("accion");

            if (!"marcarPagada".equals(accion)) {
                throw new IllegalArgumentException(
                        "Acción no permitida."
                );
            }

            String valor = request.getParameter("id");

            if (valor == null || valor.isBlank()) {
                throw new IllegalArgumentException(
                        "Debes seleccionar una comisión."
                );
            }

            Long comisionId;

            try {
                comisionId = Long.valueOf(valor);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "ID de comisión inválido."
                );
            }

            if (comisionId <= 0) {
                throw new IllegalArgumentException(
                        "ID de comisión inválido."
                );
            }

            new ComisionService(Jpa.factory())
                    .marcarPagada(comisionId);

            request.getSession().setAttribute(
                    "flashExito",
                    "Comisión marcada como pagada correctamente."
            );

        } catch (IllegalArgumentException | IllegalStateException e) {

            request.getSession().setAttribute(
                    "flashError",
                    e.getMessage()
            );

        } catch (RuntimeException e) {
            throw new ServletException(
                    "No se pudo actualizar la comisión.", e
            );
        }

        response.sendRedirect(
                request.getContextPath() + "/admin/comisiones"
        );
    }

    private String formatear(LocalDateTime fecha) {

        return fecha == null
                ? "—"
                : fecha.format(FORMATO);
    }
}
