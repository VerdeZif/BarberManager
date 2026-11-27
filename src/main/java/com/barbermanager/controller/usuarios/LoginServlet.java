
package com.barbermanager.controller.usuarios;

import com.barbermanager.model.Usuario;
import com.barbermanager.service.usuarios.AutenticacionService;
import com.barbermanager.util.Jpa;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final String VISTA_LOGIN =
            "/WEB-INF/views/publico/login.jsp";

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.getRequestDispatcher(VISTA_LOGIN)
                .forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("usuario");
        String textoPassword = request.getParameter("password");

        char[] password = textoPassword == null
                ? new char[0]
                : textoPassword.toCharArray();

        try {
            AutenticacionService servicio =
                    new AutenticacionService(Jpa.factory());

            Optional<Usuario> resultado =
                    servicio.autenticar(username, password);

            if (resultado.isEmpty()) {
                response.setStatus(
                        HttpServletResponse.SC_UNAUTHORIZED
                );

                request.setAttribute(
                        "mensajeError",
                        "Usuario o contraseña incorrectos, o cuenta inactiva."
                );

                doGet(request, response);
                return;
            }

            Usuario usuario = resultado.get();

            // Invalidar la sesión anterior para impedir
            // la reutilización de su identificador.
            HttpSession anterior = request.getSession(false);

            if (anterior != null) {
                anterior.invalidate();
            }

            // Crear una nueva sesión autenticada.
            HttpSession session = request.getSession(true);

            session.setMaxInactiveInterval(30 * 60);

            session.setAttribute(
                    "usuarioId",
                    usuario.getId()
            );

            session.setAttribute(
                    "rol",
                    usuario.getRol().name()
            );

            // El frontend de Gerald necesita
            // usuarioSesion.username y usuarioSesion.rol.
            session.setAttribute(
                    "usuarioSesion",
                    Map.of(
                            "username", usuario.getUsername(),
                            "rol", usuario.getRol().name()
                    )
            );

            // Por ahora redirigir al inicio.
            // Los paneles por rol se integrarán después.
            response.sendRedirect(
                    request.getContextPath() + "/"
            );

        } finally {
            Arrays.fill(password, '\0');
        }
    }
}
