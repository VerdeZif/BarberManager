
package com.barbermanager.controller.reportes;

import com.barbermanager.service.reportes.ReporteService;
import com.barbermanager.util.Jpa;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/reportes")
public class ReportesServlet extends HttpServlet {

    private static final String VISTA =
            "/WEB-INF/views/panel/reportes.jsp";

    private static final ZoneId ZONA =
            ZoneId.of("America/Lima");

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        if (!"ADMINISTRADOR".equals(
                request.getAttribute("rolActual"))) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN
            );
            return;
        }

        LocalDate hoy = LocalDate.now(ZONA);
        LocalDate desde = hoy.minusDays(29);
        LocalDate hasta = hoy;

        try {
            String inicio = request.getParameter("desde");
            String fin = request.getParameter("hasta");

            if (inicio != null && !inicio.isBlank()) {
                desde = LocalDate.parse(inicio);
            }

            if (fin != null && !fin.isBlank()) {
                hasta = LocalDate.parse(fin);
            }

            if (hasta.isBefore(desde)) {
                throw new IllegalArgumentException(
                        "La fecha final no puede ser anterior a la inicial."
                );
            }

            if (ChronoUnit.DAYS.between(desde, hasta) > 365) {
                throw new IllegalArgumentException(
                        "El período máximo es de 366 días."
                );
            }

        } catch (RuntimeException e) {
            desde = hoy.minusDays(29);
            hasta = hoy;

            request.setAttribute(
                    "mensajeError",
                    "Período inválido. Se muestran los últimos 30 días."
            );
        }

        LocalDateTime fechaInicio = desde.atStartOfDay();
        LocalDateTime fechaFin =
                hasta.plusDays(1).atStartOfDay();

        request.setAttribute("desde", desde.toString());
        request.setAttribute("hasta", hasta.toString());

        ReporteService servicio =
                new ReporteService(Jpa.factory());

        try {
            request.setAttribute(
                    "totalVentas",
                    servicio.ventasSimuladas(
                            fechaInicio, fechaFin
                    )
            );

            List<Map<String, Object>> estados =
                    new ArrayList<>();

            for (ReporteService.ConteoEstado dato :
                    servicio.citasPorEstado(
                            fechaInicio, fechaFin)) {

                estados.add(Map.of(
                        "estado", dato.estado(),
                        "cantidad", dato.cantidad()
                ));
            }

            request.setAttribute("estadosCitas", estados);

            List<Map<String, Object>> stock =
                    new ArrayList<>();

            for (ReporteService.ProductoBajo producto :
                    servicio.stockBajo()) {

                stock.add(Map.of(
                        "id", producto.id(),
                        "nombre", producto.nombre(),
                        "stock", producto.stock(),
                        "minimo", producto.minimo()
                ));
            }

            request.setAttribute("stockBajo", stock);
            request.setAttribute(
                    "cantidadStockBajo",
                    stock.size()
            );

            cargarReportesAdicionales(
                    request,
                    fechaInicio,
                    fechaFin
            );

        } catch (RuntimeException e) {
            throw new ServletException(
                    "No se pudieron generar los reportes.", e
            );
        }

        request.getRequestDispatcher(VISTA)
                .forward(request, response);
    }

    private void cargarReportesAdicionales(
            HttpServletRequest request,
            LocalDateTime desde,
            LocalDateTime hasta
    ) {
        EntityManager em =
                Jpa.factory().createEntityManager();

        try {
            // Cobros simulados registrados por fecha.
            BigDecimal cobros = em.createQuery(
                            "SELECT SUM(p.monto) FROM Pago p " +
                                    "WHERE p.fecha >= :desde " +
                                    "AND p.fecha < :hasta",
                            BigDecimal.class
                    )
                    .setParameter("desde", desde)
                    .setParameter("hasta", hasta)
                    .getSingleResult();

            request.setAttribute(
                    "totalCobros",
                    cobros == null ? BigDecimal.ZERO : cobros
            );

            // Cobros agrupados por método de pago.
            List<Object[]> resultadoPagos =
                    em.createQuery(
                                    "SELECT p.metodo, COUNT(p.id), SUM(p.monto) " +
                                            "FROM Pago p " +
                                            "WHERE p.fecha >= :desde " +
                                            "AND p.fecha < :hasta " +
                                            "GROUP BY p.metodo " +
                                            "ORDER BY p.metodo",
                                    Object[].class
                            )
                            .setParameter("desde", desde)
                            .setParameter("hasta", hasta)
                            .getResultList();

            List<Map<String, Object>> metodos =
                    new ArrayList<>();

            for (Object[] fila : resultadoPagos) {
                metodos.add(Map.of(
                        "metodo", fila[0],
                        "cantidad", fila[1],
                        "monto", fila[2]
                ));
            }

            request.setAttribute("metodosPago", metodos);

            // Comisiones generadas en el período.
            BigDecimal comisiones = em.createQuery(
                            "SELECT SUM(c.monto) FROM Comision c " +
                                    "WHERE c.fechaGeneracion >= :desde " +
                                    "AND c.fechaGeneracion < :hasta",
                            BigDecimal.class
                    )
                    .setParameter("desde", desde)
                    .setParameter("hasta", hasta)
                    .getSingleResult();

            request.setAttribute(
                    "totalComisiones",
                    comisiones == null
                            ? BigDecimal.ZERO
                            : comisiones
            );

            // Cinco servicios con más reservas válidas.
            List<Object[]> resultadoServicios =
                    em.createQuery(
                                    "SELECT s.nombre, COUNT(c.id) " +
                                            "FROM Cita c JOIN c.servicio s " +
                                            "WHERE c.inicio >= :desde " +
                                            "AND c.inicio < :hasta " +
                                            "AND c.estado NOT IN " +
                                            "('CANCELADA', 'NO_ASISTIO') " +
                                            "GROUP BY s.id, s.nombre " +
                                            "ORDER BY COUNT(c.id) DESC",
                                    Object[].class
                            )
                            .setParameter("desde", desde)
                            .setParameter("hasta", hasta)
                            .setMaxResults(5)
                            .getResultList();

            List<Map<String, Object>> servicios =
                    new ArrayList<>();

            for (Object[] fila : resultadoServicios) {
                servicios.add(Map.of(
                        "nombre", fila[0],
                        "cantidad", fila[1]
                ));
            }

            request.setAttribute(
                    "topServicios",
                    servicios
            );

        } finally {
            em.close();
        }
    }
}
