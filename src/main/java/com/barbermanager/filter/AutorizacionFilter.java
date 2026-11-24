package com.barbermanager.filter;

import com.barbermanager.model.Usuario;
import com.barbermanager.util.Jpa;
import jakarta.persistence.EntityManager;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Set;

/** Verifica el rol actual en base de datos, no solo el guardado en sesión. */
@WebFilter(urlPatterns = {"/admin/*", "/recepcion/*", "/barbero/*", "/reservas/*", "/mis-citas/*", "/mis-tickets/*"})
public class AutorizacionFilter implements Filter {
    @Override
    public void doFilter(ServletRequest solicitud, ServletResponse respuesta, FilterChain cadena)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) solicitud;
        HttpServletResponse response = (HttpServletResponse) respuesta;
        HttpSession session = request.getSession(false);
        Object id = session == null ? null : session.getAttribute("usuarioId");
        if (!(id instanceof Long)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        EntityManager em = Jpa.factory().createEntityManager();
        String rol;
        try {
            Usuario usuario = em.find(Usuario.class, (Long) id);
            if (usuario == null || !usuario.isActivo()) {
                session.invalidate();
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            rol = usuario.getRol().name();
        } finally { em.close(); }
        String ruta = request.getRequestURI().substring(request.getContextPath().length());
        boolean permitido = ruta.startsWith("/admin/") ? rol.equals("ADMINISTRADOR")
                : ruta.startsWith("/recepcion/") ? Set.of("ADMINISTRADOR", "RECEPCIONISTA").contains(rol)
                : ruta.startsWith("/barbero/") ? Set.of("ADMINISTRADOR", "RECEPCIONISTA", "BARBERO").contains(rol)
                : rol.equals("CLIENTE");
        if (!permitido) { response.sendError(HttpServletResponse.SC_FORBIDDEN); return; }
        request.setAttribute("rolActual", rol);
        cadena.doFilter(request, response);
    }
}
