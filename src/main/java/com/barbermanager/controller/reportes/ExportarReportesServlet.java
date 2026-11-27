
package com.barbermanager.controller.reportes;

import com.barbermanager.util.Jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@WebServlet("/admin/reportes/exportar")
public class ExportarReportesServlet extends HttpServlet {

    private static final ZoneId ZONA =
            ZoneId.of("America/Lima");

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final Set<String> TIPOS =
            Set.of("ventas", "cobros", "citas",
                    "comisiones", "servicios", "stock");

    private static final int LIMITE = 10000;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        // La ruta también está protegida por AutorizacionFilter.
        if (!"ADMINISTRADOR".equals(
                request.getAttribute("rolActual"))) {

            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String tipo = request.getParameter("tipo");

        if (tipo == null || !TIPOS.contains(tipo)) {
            response.sendError(400, "Tipo de reporte no válido.");
            return;
        }

        LocalDate desde;
        LocalDate hasta;

        try {
            LocalDate hoy = LocalDate.now(ZONA);

            String inicio = request.getParameter("desde");
            String fin = request.getParameter("hasta");

            desde = inicio == null || inicio.isBlank()
                    ? hoy.minusDays(29)
                    : LocalDate.parse(inicio);

            hasta = fin == null || fin.isBlank()
                    ? hoy
                    : LocalDate.parse(fin);

            if (hasta.isBefore(desde) ||
                    ChronoUnit.DAYS.between(desde, hasta) > 365) {

                throw new IllegalArgumentException(
                        "El período debe contener entre 1 y 366 días."
                );
            }

        } catch (DateTimeException |
                 IllegalArgumentException e) {

            response.sendError(400, "Período de fechas inválido.");
            return;
        }

        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.plusDays(1).atStartOfDay();

        String[] encabezados = encabezados(tipo);

        List<Object[]> filas;

        try (EntityManager em =
                     Jpa.factory().createEntityManager()) {

            String jpql = consulta(tipo);

            TypedQuery<Object[]> query =
                    em.createQuery(jpql, Object[].class);

            // Stock bajo es una fotografía del inventario actual.
            // No depende del período seleccionado.
            if (!"stock".equals(tipo)) {
                query.setParameter("desde", inicio);
                query.setParameter("hasta", fin);
            }

            query.setMaxResults(
                    "servicios".equals(tipo) ? 5 : LIMITE + 1
            );

            filas = query.getResultList();

        } catch (RuntimeException e) {

            throw new ServletException(
                    "No se pudo exportar el reporte.", e
            );
        }

        if (filas.size() > LIMITE) {
            response.sendError(
                    413,
                    "El reporte supera los 10000 registros. " +
                            "Selecciona un período más corto."
            );
            return;
        }

        StringBuilder csv = new StringBuilder();

        agregarFila(csv, encabezados);

        for (Object[] fila : filas) {
            agregarFila(csv, fila);
        }

        byte[] contenido =
                csv.toString().getBytes(StandardCharsets.UTF_8);

        String nombre =
                "BarberManager_" + tipo + "_" +
                        desde + "_" + hasta + ".csv";

        response.setContentType("text/csv; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=\"" + nombre + "\""
        );

        response.setHeader(
                "Cache-Control",
                "no-store, no-cache, must-revalidate"
        );

        response.setHeader("X-Content-Type-Options", "nosniff");

        response.setContentLength(contenido.length + 3);

        // BOM UTF-8: facilita reconocer tildes y ñ en Excel.
        var salida = response.getOutputStream();

        salida.write(0xEF);
        salida.write(0xBB);
        salida.write(0xBF);

        salida.write(contenido);
        salida.flush();
    }

    private String consulta(String tipo) {

        return switch (tipo) {

            case "ventas" ->
                    "SELECT v.numeroTicket, v.fecha, " +
                            "v.total, v.estadoPago " +
                            "FROM Venta v " +
                            "WHERE v.estadoPago = 'PAGADA' " +
                            "AND v.fecha >= :desde " +
                            "AND v.fecha < :hasta " +
                            "ORDER BY v.fecha DESC, v.id DESC";

            case "cobros" ->
                    "SELECT p.id, v.numeroTicket, p.fecha, " +
                            "p.metodo, p.monto " +
                            "FROM Pago p JOIN p.venta v " +
                            "WHERE p.fecha >= :desde " +
                            "AND p.fecha < :hasta " +
                            "ORDER BY p.fecha DESC, p.id DESC";

            case "citas" ->
                    "SELECT c.id, c.inicio, s.nombre, " +
                            "b.nombres, b.apellidos, c.estado " +
                            "FROM Cita c " +
                            "JOIN c.servicio s " +
                            "JOIN c.barbero b " +
                            "WHERE c.inicio >= :desde " +
                            "AND c.inicio < :hasta " +
                            "ORDER BY c.inicio DESC, c.id DESC";

            case "comisiones" ->
                    "SELECT c.id, c.fechaGeneracion, " +
                            "b.nombres, b.apellidos, " +
                            "c.porcentaje, c.monto, c.estado " +
                            "FROM Comision c JOIN c.barbero b " +
                            "WHERE c.fechaGeneracion >= :desde " +
                            "AND c.fechaGeneracion < :hasta " +
                            "ORDER BY c.fechaGeneracion DESC, c.id DESC";

            case "servicios" ->
                    "SELECT s.nombre, COUNT(c.id) " +
                            "FROM Cita c JOIN c.servicio s " +
                            "WHERE c.inicio >= :desde " +
                            "AND c.inicio < :hasta " +
                            "AND c.estado NOT IN " +
                            "('CANCELADA', 'NO_ASISTIO') " +
                            "GROUP BY s.id, s.nombre " +
                            "ORDER BY COUNT(c.id) DESC";

            case "stock" ->
                    "SELECT p.nombre, p.stockActual, p.stockMinimo " +
                            "FROM Producto p " +
                            "WHERE p.activo = true " +
                            "AND p.stockActual <= p.stockMinimo " +
                            "ORDER BY p.nombre";

            default -> throw new IllegalArgumentException(
                    "Reporte desconocido."
            );
        };
    }

    private String[] encabezados(String tipo) {

        return switch (tipo) {

            case "ventas" -> new String[]{
                    "Ticket", "Fecha", "Total (S/)", "Estado"
            };

            case "cobros" -> new String[]{
                    "ID pago", "Ticket", "Fecha",
                    "Método", "Monto (S/)"
            };

            case "citas" -> new String[]{
                    "ID cita", "Fecha y hora", "Servicio",
                    "Nombres del barbero", "Apellidos del barbero",
                    "Estado"
            };

            case "comisiones" -> new String[]{
                    "ID comisión", "Fecha de generación",
                    "Nombres", "Apellidos",
                    "Porcentaje (%)", "Comisión (S/)", "Estado"
            };

            case "servicios" -> new String[]{
                    "Servicio", "Cantidad de reservas"
            };

            case "stock" -> new String[]{
                    "Producto", "Stock actual", "Stock mínimo"
            };

            default -> throw new IllegalArgumentException(
                    "Reporte desconocido."
            );
        };
    }

    private void agregarFila(
            StringBuilder csv,
            Object[] valores
    ) {

        for (int i = 0; i < valores.length; i++) {

            if (i > 0) {
                csv.append(';');
            }

            csv.append(convertirCelda(valores[i]));
        }

        csv.append("\r\n");
    }

    private String convertirCelda(Object valor) {

        String texto;

        if (valor == null) {
            texto = "";

        } else if (valor instanceof LocalDateTime fecha) {
            texto = fecha.format(FORMATO);

        } else if (valor instanceof BigDecimal decimal) {
            texto = decimal.toPlainString();

        } else {
            texto = String.valueOf(valor);
        }

        texto = texto.replace("\r", " ")
                .replace("\n", " ");

        // Evitar que Excel interprete textos provenientes
        // de la base de datos como fórmulas.
        String inicio = texto.stripLeading();

        if (!inicio.isEmpty() &&
                "=+-@".indexOf(inicio.charAt(0)) >= 0) {
            texto = "'" + texto;
        }

        return "\"" + texto.replace("\"", "\"\"") + "\"";
    }
}
