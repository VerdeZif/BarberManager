
package com.barbermanager.controller.usuarios;

import com.barbermanager.controller.ControllerBase;
import com.barbermanager.model.Usuario;
import com.barbermanager.service.usuarios.UsuarioService;
import com.barbermanager.util.Jpa;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/usuarios")
public class UsuarioAdminServlet extends ControllerBase {

    private static final String VISTA =
            "/WEB-INF/views/panel/usuarios.jsp";

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        EntityManager em = null;

        try {
            em = Jpa.factory().createEntityManager();

            List<Usuario> usuarios = em.createQuery(
                            "SELECT u FROM Usuario u ORDER BY u.id DESC",
                            Usuario.class
                    )
                    .setMaxResults(100)
                    .getResultList();

            request.setAttribute("usuarios", usuarios);

        } catch (RuntimeException e) {
            throw new ServletException(
                    "No se pudo cargar el listado de usuarios.", e
            );
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        request.getRequestDispatcher(VISTA)
                .forward(request, response);
    }

    // Conservamos la funcionalidad original
    // de Alexander para cambiar el estado.
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        try {
            String activo = request.getParameter("activo");

            if (!"true".equals(activo) &&
                    !"false".equals(activo)) {

                throw new IllegalArgumentException(
                        "Estado inválido"
                );
            }

            new UsuarioService(Jpa.factory())
                    .cambiarEstado(
                            id(request, "id", true),
                            Boolean.parseBoolean(activo)
                    );

            response.setStatus(
                    HttpServletResponse.SC_NO_CONTENT
            );

        } catch (RuntimeException e) {
            error(response, e);
        }
    }
}
