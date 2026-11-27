
package com.barbermanager.controller.usuarios;

import com.barbermanager.service.usuarios.AutenticacionService;
import com.barbermanager.util.Jpa;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@WebServlet("/registro")
public class RegistroServlet extends HttpServlet {

    private static final String VISTA_REGISTRO =
            "/WEB-INF/views/publico/registro.jsp";

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.getRequestDispatcher(VISTA_REGISTRO)
                .forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String nombres = request.getParameter("nombres");
        String apellidos = request.getParameter("apellidos");
        String telefono = request.getParameter("telefono");

        String textoPassword = request.getParameter("password");
        String textoConfirmacion =
                request.getParameter("confirmarPassword");

        char[] password = textoPassword == null
                ? new char[0]
                : textoPassword.toCharArray();

        char[] confirmacion = textoConfirmacion == null
                ? new char[0]
                : textoConfirmacion.toCharArray();

        try {
            if (!Arrays.equals(password, confirmacion)) {

                response.setStatus(
                        HttpServletResponse.SC_BAD_REQUEST
                );

                request.setAttribute(
                        "errores",
                        Map.of(
                                "confirmarPassword",
                                "Las contraseñas no coinciden."
                        )
                );

                request.setAttribute(
                        "mensajeError",
                        "Verifica que ambas contraseñas sean iguales."
                );

                doGet(request, response);
                return;
            }

            AutenticacionService servicio =
                    new AutenticacionService(Jpa.factory());

            servicio.registrar(
                    username,
                    email,
                    password,
                    nombres,
                    apellidos,
                    telefono
            );

            response.sendRedirect(
                    request.getContextPath() + "/login?registro=ok"
            );

        } catch (IllegalArgumentException e) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST
            );

            request.setAttribute(
                    "mensajeError",
                    e.getMessage()
            );

            doGet(request, response);

        } finally {
            Arrays.fill(password, '\0');
            Arrays.fill(confirmacion, '\0');
        }
    }
}
