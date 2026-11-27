
package com.barbermanager.controller.publico;

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

@WebServlet("/inicio")
public class InicioPublicoServlet extends HttpServlet {

    @Override
    protected void doGet(
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
                    )
                    .setMaxResults(6)
                    .getResultList();

            request.setAttribute("servicios", servicios);

        } catch (RuntimeException e) {
            throw new ServletException(
                    "No se pudieron cargar los servicios de inicio.",
                    e
            );

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        request.getRequestDispatcher(
                "/WEB-INF/views/publico/inicio.jsp"
        ).forward(request, response);
    }
}
