package com.barbermanager.controller.usuarios;

import com.barbermanager.service.usuarios.AutenticacionService;
import com.barbermanager.util.Jpa;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Arrays;

@WebServlet("/registro")
public class RegistroServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/registro.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String texto = request.getParameter("password");
        char[] password = texto == null ? new char[0] : texto.toCharArray();
        try {
            new AutenticacionService(Jpa.factory()).registrar(request.getParameter("username"),
                    request.getParameter("email"), password, request.getParameter("nombres"),
                    request.getParameter("apellidos"), request.getParameter("telefono"));
            response.sendRedirect(request.getContextPath() + "/login?registrado=1");
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", e.getMessage());
            doGet(request, response);
        } finally {
            Arrays.fill(password, '\0');
        }
    }
}
