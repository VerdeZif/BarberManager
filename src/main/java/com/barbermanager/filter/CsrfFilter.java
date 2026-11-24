package com.barbermanager.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@WebFilter("/*")
public class CsrfFilter implements Filter {
    private final SecureRandom random = new SecureRandom();

    @Override
    public void doFilter(ServletRequest solicitud, ServletResponse respuesta, FilterChain cadena)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) solicitud;
        HttpServletResponse response = (HttpServletResponse) respuesta;
        request.setCharacterEncoding("UTF-8");
        String metodo = request.getMethod();
        if (!metodo.equals("GET") && !metodo.equals("HEAD") && !metodo.equals("OPTIONS")) {
            HttpSession session = request.getSession(false);
            String esperado = session == null ? null : (String) session.getAttribute("csrfToken");
            String recibido = request.getParameter("csrfToken");
            if (esperado == null || recibido == null || !MessageDigest.isEqual(
                    esperado.getBytes(StandardCharsets.UTF_8), recibido.getBytes(StandardCharsets.UTF_8))) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        } else if (metodo.equals("GET")) {
            HttpSession session = request.getSession(true);
            if (session.getAttribute("csrfToken") == null) {
                byte[] bytes = new byte[32];
                random.nextBytes(bytes);
                session.setAttribute("csrfToken", Base64.getUrlEncoder().withoutPadding().encodeToString(bytes));
            }
        }
        cadena.doFilter(request, response);
    }
}
