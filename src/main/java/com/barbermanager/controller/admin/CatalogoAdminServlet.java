
package com.barbermanager.controller.admin;

import com.barbermanager.model.Barbero;
import com.barbermanager.model.Servicio;
import com.barbermanager.service.barberos.BarberoService;
import com.barbermanager.service.servicios.ServicioService;
import com.barbermanager.util.Jpa;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalTime;

@WebServlet(urlPatterns = {
        "/admin/barberos",
        "/admin/servicios"
})
public class CatalogoAdminServlet extends HttpServlet {

    private static final String VISTA =
            "/WEB-INF/views/panel/catalogos.jsp";

    private boolean esBarberos(HttpServletRequest request) {
        return "/admin/barberos".equals(request.getServletPath());
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        cargarVista(request, response);
    }

    private void cargarVista(HttpServletRequest request,
                             HttpServletResponse response)
            throws ServletException, IOException {

        boolean barberos = esBarberos(request);
        request.setAttribute("moduloBarberos", barberos);

        EntityManager em = null;

        try {
            em = Jpa.factory().createEntityManager();

            if (barberos) {
                request.setAttribute("registros",
                        em.createQuery(
                                "SELECT b FROM Barbero b " +
                                        "ORDER BY b.nombres, b.apellidos",
                                Barbero.class
                        ).getResultList());
            } else {
                request.setAttribute("registros",
                        em.createQuery(
                                "SELECT s FROM Servicio s ORDER BY s.nombre",
                                Servicio.class
                        ).getResultList());
            }

            String editar = request.getParameter("editar");

            if (editar != null && !editar.isBlank()) {
                Long id = Long.valueOf(editar);

                Object seleccionado = barberos
                        ? em.find(Barbero.class, id)
                        : em.find(Servicio.class, id);

                if (seleccionado == null) {
                    throw new IllegalArgumentException(
                            "Registro no encontrado."
                    );
                }

                request.setAttribute("seleccionado", seleccionado);
            }

        } catch (IllegalArgumentException e) {
            request.setAttribute("mensajeError", e.getMessage());

        } catch (RuntimeException e) {
            throw new ServletException(
                    "No se pudo cargar el catálogo.", e
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
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String valorId = request.getParameter("id");

            Long id = valorId == null || valorId.isBlank()
                    ? null
                    : Long.valueOf(valorId);

            boolean activo =
                    "true".equals(request.getParameter("activo"));

            if (esBarberos(request)) {

                String nombres = request.getParameter("nombres");
                String apellidos = request.getParameter("apellidos");
                String dias = request.getParameter("diasLaborables");

                LocalTime inicio = LocalTime.parse(
                        request.getParameter("horaInicio")
                );

                LocalTime fin = LocalTime.parse(
                        request.getParameter("horaFin")
                );

                BigDecimal comision = new BigDecimal(
                        request.getParameter("porcentajeComision")
                );

                new BarberoService(Jpa.factory()).guardar(
                        id, nombres, apellidos, dias,
                        inicio, fin, comision, activo
                );

            } else {

                String nombre = request.getParameter("nombre");
                String descripcion = request.getParameter("descripcion");

                BigDecimal precio = new BigDecimal(
                        request.getParameter("precio")
                );

                int duracion = Integer.parseInt(
                        request.getParameter("duracionMinutos")
                );

                new ServicioService(Jpa.factory()).guardar(
                        id, nombre, descripcion,
                        precio, duracion, activo
                );
            }

            request.getSession().setAttribute(
                    "flashExito",
                    id == null
                            ? "Registro creado correctamente."
                            : "Registro actualizado correctamente."
            );

            response.sendRedirect(
                    request.getContextPath() + request.getServletPath()
            );

        } catch (IllegalArgumentException | DateTimeException e) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("mensajeError",
                    "Revisa los datos ingresados: " + e.getMessage());
            cargarVista(request, response);

        } catch (RuntimeException e) {
            throw new ServletException(
                    "No se pudo guardar el registro.", e
            );
        }
    }
}
