package com.barbermanager.controller.usuarios;

import com.barbermanager.model.Usuario;
import com.barbermanager.service.usuarios.AutenticacionService;
import com.barbermanager.util.Jpa;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String texto = request.getParameter("password");
        char[] password = texto == null ? new char[0] : texto.toCharArray();
        try {
            Optional<Usuario> resultado = new AutenticacionService(Jpa.factory()).autenticar(request.getParameter("username"), password);
            if (resultado.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                request.setAttribute("error", "Credenciales inválidas o cuenta inactiva.");
                doGet(request, response);
                return;
            }
            Usuario usuario = resultado.get();
            HttpSession anterior = request.getSession(false);
            if (anterior != null) anterior.invalidate();
            HttpSession nueva = request.getSession(true);
            nueva.setAttribute("usuarioId", usuario.getId());
            nueva.setAttribute("rol", usuario.getRol().name());
            response.sendRedirect(request.getContextPath() + "/");
        } finally {
            Arrays.fill(password, '\0');
        }
    }
}
