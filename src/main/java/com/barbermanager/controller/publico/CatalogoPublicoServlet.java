
package com.barbermanager.controller.publico;

import com.barbermanager.model.Barbero;
import com.barbermanager.model.Servicio;
import com.barbermanager.util.Jpa;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(
        name = "CatalogoPublicoServlet",
        urlPatterns = {"/servicios", "/barberos"}
)
public class CatalogoPublicoServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        String ruta = request.getServletPath();

        if ("/servicios".equals(ruta)) {
            mostrarServicios(request, response);

        } else if ("/barberos".equals(ruta)) {
            mostrarBarberos(request, response);

        } else {
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND
            );
        }
    }

    private void mostrarServicios(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        EntityManager em = null;

        try {
            em = Jpa.factory().createEntityManager();

            List<Servicio> servicios = em.createQuery(
                    "SELECT s FROM Servicio s " +
                            "WHERE s.activo = true " +
                            "ORDER BY s.nombre",
                    Servicio.class
            ).getResultList();

            request.setAttribute("servicios", servicios);

        } catch (RuntimeException e) {
            throw new ServletException(
                    "No se pudo cargar el catálogo de servicios.",
                    e
            );

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        request.getRequestDispatcher(
                "/WEB-INF/views/publico/servicios.jsp"
        ).forward(request, response);
    }

    private void mostrarBarberos(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        EntityManager em = null;

        try {
            em = Jpa.factory().createEntityManager();

            List<Barbero> barberos = em.createQuery(
                    "SELECT b FROM Barbero b " +
                            "WHERE b.activo = true " +
                            "ORDER BY b.nombres, b.apellidos",
                    Barbero.class
            ).getResultList();

            request.setAttribute("barberos", barberos);

        } catch (RuntimeException e) {
            throw new ServletException(
                    "No se pudo cargar el catálogo de barberos.",
                    e
            );

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        request.getRequestDispatcher(
                "/WEB-INF/views/publico/barberos.jsp"
        ).forward(request, response);
    }
}
