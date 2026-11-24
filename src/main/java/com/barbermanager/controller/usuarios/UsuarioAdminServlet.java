package com.barbermanager.controller.usuarios;

import com.barbermanager.controller.ControllerBase;
import com.barbermanager.service.usuarios.UsuarioService;
import com.barbermanager.util.Jpa;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/usuarios")
public class UsuarioAdminServlet extends ControllerBase {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String activo = request.getParameter("activo");
            if (!"true".equals(activo) && !"false".equals(activo))
                throw new IllegalArgumentException("Estado inválido");
            new UsuarioService(Jpa.factory()).cambiarEstado(id(request, "id", true), Boolean.parseBoolean(activo));
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (RuntimeException e) {
            error(response, e);
        }
    }
}
