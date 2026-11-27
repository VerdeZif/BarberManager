
package com.barbermanager.controller.reportes;

import com.barbermanager.util.Jpa;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@WebServlet("/admin/reportes/excel")
public class ExportarExcelServlet extends HttpServlet {

    private static final ZoneId ZONA =
            ZoneId.of("America/Lima");

    private static final int MAX_FILAS = 5000;

    private record Reporte(
            String nombre,
            String[] columnas,
            String consulta,
            boolean usaFechas
    ) {}

    private static final List<Reporte> REPORTES = List.of(

            new Reporte(
                    "Ventas",
                    new String[]{
                            "Ticket", "Fecha",
                            "Total (S/)", "Estado"
                    },
                    """
                    SELECT v.numeroTicket, v.fecha,
                           v.total, v.estadoPago
                    FROM Venta v
                    WHERE v.estadoPago = 'PAGADA'
                      AND v.fecha >= :desde
                      AND v.fecha < :hasta
                    ORDER BY v.fecha DESC, v.id DESC
                    """,
                    true
            ),

            new Reporte(
                    "Cobros",
                    new String[]{
                            "ID pago", "Ticket", "Fecha",
                            "Método", "Importe (S/)"
                    },
                    """
                    SELECT p.id, v.numeroTicket,
                           p.fecha, p.metodo, p.monto
                    FROM Pago p JOIN p.venta v
                    WHERE p.fecha >= :desde
                      AND p.fecha < :hasta
                    ORDER BY p.fecha DESC, p.id DESC
                    """,
                    true
            ),

            new Reporte(
                    "Citas",
                    new String[]{
                            "ID cita", "Fecha y hora",
                            "Servicio", "Nombres del barbero",
                            "Apellidos del barbero", "Estado"
                    },
                    """
                    SELECT c.id, c.inicio, s.nombre,
                           b.nombres, b.apellidos, c.estado
                    FROM Cita c
                    JOIN c.servicio s
                    JOIN c.barbero b
                    WHERE c.inicio >= :desde
                      AND c.inicio < :hasta
                    ORDER BY c.inicio DESC, c.id DESC
                    """,
                    true
            ),

            new Reporte(
                    "Comisiones",
                    new String[]{
                            "ID comisión", "Fecha",
                            "Nombres del barbero",
                            "Apellidos del barbero",
                            "Porcentaje (%)",
                            "Comisión (S/)", "Estado"
                    },
                    """
                    SELECT c.id, c.fechaGeneracion,
                           b.nombres, b.apellidos,
                           c.porcentaje, c.monto, c.estado
                    FROM Comision c
                    JOIN c.barbero b
                    WHERE c.fechaGeneracion >= :desde
                      AND c.fechaGeneracion < :hasta
                    ORDER BY c.fechaGeneracion DESC, c.id DESC
                    """,
                    true
            ),

            new Reporte(
                    "Servicios",
                    new String[]{
                            "Servicio", "Reservas"
                    },
                    """
                    SELECT s.nombre, COUNT(c.id)
                    FROM Cita c
                    JOIN c.servicio s
                    WHERE c.inicio >= :desde
                      AND c.inicio < :hasta
                      AND c.estado NOT IN
                          ('CANCELADA', 'NO_ASISTIO')
                    GROUP BY s.id, s.nombre
                    ORDER BY COUNT(c.id) DESC
                    """,
                    true
            ),

            new Reporte(
                    "Stock bajo",
                    new String[]{
                            "Producto", "Stock actual",
                            "Stock mínimo"
                    },
                    """
                    SELECT p.nombre, p.stockActual,
                           p.stockMinimo
                    FROM Producto p
                    WHERE p.activo = true
                      AND p.stockActual <= p.stockMinimo
                    ORDER BY p.nombre
                    """,
                    false
            )
    );

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

        LocalDate desde;
        LocalDate hasta;

        try {
            LocalDate hoy = LocalDate.now(ZONA);

            String valorDesde = request.getParameter("desde");
            String valorHasta = request.getParameter("hasta");

            desde = valorDesde == null || valorDesde.isBlank()
                    ? hoy.minusDays(29)
                    : LocalDate.parse(valorDesde);

            hasta = valorHasta == null || valorHasta.isBlank()
                    ? hoy
                    : LocalDate.parse(valorHasta);

            long dias = ChronoUnit.DAYS.between(desde, hasta);

            if (dias < 0 || dias > 365) {
                throw new IllegalArgumentException(
                        "Período inválido."
                );
            }

        } catch (DateTimeException | IllegalArgumentException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Selecciona un período válido de hasta 366 días."
            );
            return;
        }

        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.plusDays(1).atStartOfDay();

        byte[] archivo;

        try (
                XSSFWorkbook libro = new XSSFWorkbook();
                ByteArrayOutputStream memoria =
                        new ByteArrayOutputStream()
        ) {

            Estilos estilos = new Estilos(libro);

            EntityManager em =
                    Jpa.factory().createEntityManager();

            try {
                for (Reporte reporte : REPORTES) {

                    var consulta = em.createQuery(
                            reporte.consulta(),
                            Object[].class
                    );

                    if (reporte.usaFechas()) {
                        consulta.setParameter("desde", inicio);
                        consulta.setParameter("hasta", fin);
                    }

                    boolean esTopServicios =
                            "Servicios".equals(reporte.nombre());

                    consulta.setMaxResults(
                            esTopServicios ? 5 : MAX_FILAS + 1
                    );

                    List<Object[]> datos =
                            consulta.getResultList();

                    if (!esTopServicios &&
                            datos.size() > MAX_FILAS) {

                        response.sendError(
                                413,
                                "El reporte " + reporte.nombre() +
                                        " supera los " + MAX_FILAS +
                                        " registros. Reduce el período."
                        );
                        return;
                    }

                    agregarHoja(
                            libro,
                            reporte,
                            datos,
                            estilos,
                            desde,
                            hasta
                    );
                }

            } finally {
                em.close();
            }

            libro.setActiveSheet(0);
            libro.write(memoria);
            archivo = memoria.toByteArray();

        } catch (RuntimeException e) {

            throw new ServletException(
                    "No se pudo generar el Excel de reportes.", e
            );
        }

        String nombreArchivo =
                "BarberManager_Reportes_" +
                        desde + "_" + hasta + ".xlsx";

        response.setContentType(
                "application/vnd.openxmlformats-officedocument." +
                        "spreadsheetml.sheet"
        );

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=\"" + nombreArchivo + "\""
        );

        response.setHeader(
                "Cache-Control", "no-store"
        );

        response.setHeader(
                "X-Content-Type-Options", "nosniff"
        );

        response.setContentLength(archivo.length);
        response.getOutputStream().write(archivo);
    }

    private void agregarHoja(
            XSSFWorkbook libro,
            Reporte reporte,
            List<Object[]> datos,
            Estilos estilos,
            LocalDate desde,
            LocalDate hasta
    ) {

        Sheet hoja = libro.createSheet(reporte.nombre());

        int totalColumnas = reporte.columnas().length;

        Row titulo = hoja.createRow(0);
        titulo.setHeightInPoints(34);

        Cell celdaTitulo = titulo.createCell(0);
        celdaTitulo.setCellValue(
                "BARBERMANAGER | " + reporte.nombre().toUpperCase()
        );
        celdaTitulo.setCellStyle(estilos.titulo);

        hoja.addMergedRegion(
                new CellRangeAddress(
                        0, 0, 0, totalColumnas - 1
                )
        );

        Row periodo = hoja.createRow(1);

        Cell celdaPeriodo = periodo.createCell(0);

        celdaPeriodo.setCellValue(
                reporte.usaFechas()
                        ? "Período: " + desde + " al " + hasta
                        : "Inventario actual al " +
                        LocalDate.now(ZONA)
        );

        celdaPeriodo.setCellStyle(estilos.subtitulo);

        hoja.addMergedRegion(
                new CellRangeAddress(
                        1, 1, 0, totalColumnas - 1
                )
        );

        Row aviso = hoja.createRow(2);

        aviso.createCell(0).setCellValue(
                "Información académica · Operaciones simuladas"
        );

        Row encabezado = hoja.createRow(3);
        encabezado.setHeightInPoints(25);

        for (int i = 0; i < totalColumnas; i++) {

            Cell celda = encabezado.createCell(i);
            celda.setCellValue(reporte.columnas()[i]);
            celda.setCellStyle(estilos.encabezado);

            int ancho = Math.min(
                    38,
                    Math.max(
                            17,
                            reporte.columnas()[i].length() + 5
                    )
            );

            hoja.setColumnWidth(i, ancho * 256);
        }

        int numeroFila = 4;

        for (Object[] datosFila : datos) {

            Row fila = hoja.createRow(numeroFila++);

            for (int i = 0; i < totalColumnas; i++) {

                Cell celda = fila.createCell(i);
                Object valor = datosFila[i];

                if (valor == null) {
                    celda.setBlank();

                } else if (valor instanceof LocalDateTime fecha) {

                    celda.setCellValue(fecha);
                    celda.setCellStyle(estilos.fecha);

                } else if (valor instanceof BigDecimal importe) {

                    celda.setCellValue(importe.doubleValue());
                    celda.setCellStyle(estilos.decimal);

                } else if (valor instanceof Number numero) {

                    celda.setCellValue(numero.doubleValue());
                    celda.setCellStyle(estilos.entero);

                } else {

                    celda.setCellValue(textoSeguro(
                            String.valueOf(valor)
                    ));
                }
            }
        }

        hoja.createFreezePane(0, 4);

        hoja.setAutoFilter(
                new CellRangeAddress(
                        3,
                        Math.max(3, numeroFila - 1),
                        0,
                        totalColumnas - 1
                )
        );

        hoja.setZoom(90);
    }

    private String textoSeguro(String texto) {

        String limpio = texto.replace("\r", " ")
                .replace("\n", " ");

        String inicio = limpio.stripLeading();

        if (!inicio.isEmpty() &&
                "=+-@".indexOf(inicio.charAt(0)) >= 0) {

            return "'" + limpio;
        }

        return limpio;
    }

    private static final class Estilos {

        private final CellStyle titulo;
        private final CellStyle subtitulo;
        private final CellStyle encabezado;
        private final CellStyle fecha;
        private final CellStyle decimal;
        private final CellStyle entero;

        private Estilos(XSSFWorkbook libro) {

            Font fuenteTitulo = libro.createFont();
            fuenteTitulo.setBold(true);
            fuenteTitulo.setFontHeightInPoints((short) 17);
            fuenteTitulo.setColor(
                    IndexedColors.WHITE.getIndex()
            );

            XSSFCellStyle estiloTitulo =
                    libro.createCellStyle();

            estiloTitulo.setFont(fuenteTitulo);

            estiloTitulo.setFillForegroundColor(
                    new XSSFColor(
                            new Color(78, 21, 36), null
                    )
            );

            estiloTitulo.setFillPattern(
                    FillPatternType.SOLID_FOREGROUND
            );

            estiloTitulo.setVerticalAlignment(
                    VerticalAlignment.CENTER
            );

            this.titulo = estiloTitulo;

            Font fuenteSubtitulo = libro.createFont();
            fuenteSubtitulo.setBold(true);
            fuenteSubtitulo.setFontHeightInPoints((short) 11);

            CellStyle estiloSubtitulo =
                    libro.createCellStyle();

            estiloSubtitulo.setFont(fuenteSubtitulo);

            this.subtitulo = estiloSubtitulo;

            Font fuenteEncabezado = libro.createFont();
            fuenteEncabezado.setBold(true);
            fuenteEncabezado.setColor(
                    IndexedColors.WHITE.getIndex()
            );

            XSSFCellStyle estiloEncabezado =
                    libro.createCellStyle();

            estiloEncabezado.setFont(fuenteEncabezado);

            estiloEncabezado.setFillForegroundColor(
                    new XSSFColor(
                            new Color(150, 109, 57), null
                    )
            );

            estiloEncabezado.setFillPattern(
                    FillPatternType.SOLID_FOREGROUND
            );

            estiloEncabezado.setVerticalAlignment(
                    VerticalAlignment.CENTER
            );

            this.encabezado = estiloEncabezado;

            DataFormat formato = libro.createDataFormat();

            CellStyle estiloFecha =
                    libro.createCellStyle();

            estiloFecha.setDataFormat(
                    formato.getFormat("dd/mm/yyyy hh:mm")
            );

            this.fecha = estiloFecha;

            CellStyle estiloDecimal =
                    libro.createCellStyle();

            estiloDecimal.setDataFormat(
                    formato.getFormat("#,##0.00")
            );

            this.decimal = estiloDecimal;

            CellStyle estiloEntero =
                    libro.createCellStyle();

            estiloEntero.setDataFormat(
                    formato.getFormat("0")
            );

            this.entero = estiloEntero;
        }
    }
}
