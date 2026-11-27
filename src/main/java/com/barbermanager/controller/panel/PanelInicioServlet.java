
package com.barbermanager.controller.panel;

import com.barbermanager.model.Cita;
import com.barbermanager.util.Jpa;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {
        "/admin/dashboard",
        "/barbero/mis-citas"
})
public class PanelInicioServlet extends HttpServlet {

    private static final String VISTA =
            "/WEB-INF/views/panel/inicio.jsp";

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null ||
                !(session.getAttribute("usuarioId") instanceof Long)) {

            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        Long usuarioId = (Long) session.getAttribute("usuarioId");

        // El filtro de Alexander establece el rol
        // después de verificarlo en la base de datos.
        String rol = (String) request.getAttribute("rolActual");
        String ruta = request.getServletPath();

        boolean administrador = "/admin/dashboard".equals(ruta);
        boolean recepcion = "/recepcion/citas".equals(ruta);
        boolean barbero = "/barbero/mis-citas".equals(ruta);

        if (administrador && !"ADMINISTRADOR".equals(rol)
                || recepcion && !("ADMINISTRADOR".equals(rol)
                || "RECEPCIONISTA".equals(rol))
                || barbero && !"BARBERO".equals(rol)) {

            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        EntityManager em = null;

        try {
            em = Jpa.factory().createEntityManager();

            if (administrador) {

                request.setAttribute("tituloPanel", "Dashboard");
                request.setAttribute("menuActivoPanel", "dashboard");
                request.setAttribute("esAdmin", true);

                request.setAttribute("totalUsuarios",
                        contar(em, "SELECT COUNT(u) FROM Usuario u"));

                request.setAttribute("totalBarberos",
                        contar(em, "SELECT COUNT(b) FROM Barbero b WHERE b.activo = true"));

                request.setAttribute("totalServicios",
                        contar(em, "SELECT COUNT(s) FROM Servicio s WHERE s.activo = true"));

                request.setAttribute("totalCitas",
                        contar(em, "SELECT COUNT(c) FROM Cita c"));

                request.setAttribute("citas", obtenerCitas(em, null));

            } else if (recepcion) {

                request.setAttribute("tituloPanel", "Agenda de citas");
                request.setAttribute("menuActivoPanel", "citas");
                request.setAttribute("esAdmin", false);

                request.setAttribute("citas", obtenerCitas(em, null));

            } else if (barbero) {

                request.setAttribute("tituloPanel", "Mis citas");
                request.setAttribute("menuActivoPanel", "b-citas");
                request.setAttribute("esAdmin", false);

                request.setAttribute(
                        "citas",
                        obtenerCitas(em, usuarioId)
                );
            }

            request.getRequestDispatcher(VISTA)
                    .forward(request, response);

        } catch (RuntimeException e) {

            throw new ServletException(
                    "No se pudo cargar el panel de BarberManager", e
            );

        } finally {

            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    private Long contar(EntityManager em, String consulta) {
        return em.createQuery(consulta, Long.class)
                .getSingleResult();
    }

    private List<Map<String, String>> obtenerCitas(
            EntityManager em,
            Long usuarioBarberoId
    ) {

        String jpql =
                "SELECT c FROM Cita c " +
                        "JOIN FETCH c.cliente " +
                        "JOIN FETCH c.barbero " +
                        "JOIN FETCH c.servicio ";

        if (usuarioBarberoId != null) {
            jpql += "WHERE c.barbero.usuario.id = :usuarioId ";
        }

        jpql += "ORDER BY c.inicio DESC";

        var consulta = em.createQuery(jpql, Cita.class);

        if (usuarioBarberoId != null) {
            consulta.setParameter("usuarioId", usuarioBarberoId);
        }

        return consulta
                .setMaxResults(50)
                .getResultList()
                .stream()
                .map(c -> Map.of(
                        "cliente",
                        c.getCliente().getNombres() + " " +
                                c.getCliente().getApellidos(),

                        "barbero",
                        c.getBarbero().getNombres() + " " +
                                c.getBarbero().getApellidos(),

                        "servicio",
                        c.getServicio().getNombre(),

                        "fecha",
                        c.getInicio().format(FORMATO_FECHA),

                        "estado",
                        c.getEstado()
                ))
                .toList();
    }
}
