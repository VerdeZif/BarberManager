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

/**
 * Páginas públicas de solo lectura (no requieren sesión):
 *   GET /           → WEB-INF/views/publico/inicio.jsp     (atributo: servicios)
 *   GET /servicios  → WEB-INF/views/publico/servicios.jsp  (atributo: servicios)
 *   GET /barberos   → WEB-INF/views/publico/barberos.jsp   (atributo: barberos)
 * Solo muestra servicios y barberos activos. Si la base de datos no está disponible,
 * la página igual carga con su estado vacío y el error queda en el log.
 */
@WebServlet(urlPatterns = {"", "/servicios", "/barberos"})
public class PublicoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String ruta = request.getServletPath();
        String vista;
        try {
            switch (ruta) {
                case "/servicios" -> { request.setAttribute("servicios", serviciosActivos()); vista = "servicios"; }
                case "/barberos" -> { request.setAttribute("barberos", barberosActivos()); vista = "barberos"; }
                default -> { request.setAttribute("servicios", serviciosActivos()); vista = "inicio"; }
            }
        } catch (RuntimeException | LinkageError e) { // LinkageError: Jpa sin variables BARBER_DB_*
            log("No se pudo cargar la página pública " + ruta, e);
            request.setAttribute("servicios", List.of());
            request.setAttribute("barberos", List.of());
            request.setAttribute("mensajeError", "No pudimos cargar la información en este momento. Inténtalo más tarde.");
            vista = switch (ruta) { case "/servicios" -> "servicios"; case "/barberos" -> "barberos"; default -> "inicio"; };
        }
        request.getRequestDispatcher("/WEB-INF/views/publico/" + vista + ".jsp").forward(request, response);
    }

    private List<Servicio> serviciosActivos() {
        EntityManager em = Jpa.factory().createEntityManager();
        try {
            return em.createQuery("select s from Servicio s where s.activo = true order by s.precio desc, s.nombre", Servicio.class)
                     .getResultList();
        } finally { em.close(); }
    }

    private List<Barbero> barberosActivos() {
        EntityManager em = Jpa.factory().createEntityManager();
        try {
            return em.createQuery("select b from Barbero b where b.activo = true order by b.nombres, b.apellidos", Barbero.class)
                     .getResultList();
        } finally { em.close(); }
    }
}
