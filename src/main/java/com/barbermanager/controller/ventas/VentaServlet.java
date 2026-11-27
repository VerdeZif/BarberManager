
package com.barbermanager.controller.ventas;

import com.barbermanager.model.*;
import com.barbermanager.service.ventas.VentaService;
import com.barbermanager.util.Jpa;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;

@WebServlet("/recepcion/ventas")
public class VentaServlet extends HttpServlet {

    private static final String VISTA =
            "/WEB-INF/views/panel/ventas.jsp";

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        try (EntityManager em = Jpa.factory().createEntityManager()) {

            request.setAttribute("clientes", em.createQuery(
                    "SELECT c FROM Cliente c WHERE c.activo = true " +
                            "ORDER BY c.nombres, c.apellidos",
                    Cliente.class
            ).getResultList());

            request.setAttribute("productos", em.createQuery(
                    "SELECT p FROM Producto p " +
                            "WHERE p.activo = true AND p.stockActual > 0 " +
                            "ORDER BY p.nombre",
                    Producto.class
            ).getResultList());

            List<Atencion> disponibles = em.createQuery(
                    "SELECT a FROM Atencion a " +
                            "JOIN FETCH a.cita c " +
                            "JOIN FETCH c.cliente " +
                            "JOIN FETCH c.servicio " +
                            "WHERE c.estado = 'FINALIZADA' " +
                            "AND NOT EXISTS (" +
                            "SELECT v.id FROM Venta v WHERE v.atencion = a) " +
                            "ORDER BY c.inicio DESC",
                    Atencion.class
            ).getResultList();

            List<Map<String, Object>> atenciones = new ArrayList<>();

            for (Atencion a : disponibles) {
                Cita c = a.getCita();

                String etiqueta = "Cita #" + c.getId() + " — " +
                        c.getCliente().getNombres() + " " +
                        c.getCliente().getApellidos() + " — " +
                        c.getServicio().getNombre() + " — S/ " +
                        c.getPrecioReservado();

                atenciones.add(Map.of(
                        "id", a.getId(),
                        "etiqueta", etiqueta
                ));
            }

            request.setAttribute("atenciones", atenciones);

            List<Venta> ventas = em.createQuery(
                    "SELECT v FROM Venta v " +
                            "LEFT JOIN FETCH v.cliente " +
                            "ORDER BY v.id DESC",
                    Venta.class
            ).setMaxResults(100).getResultList();

            Map<Long, BigDecimal> pagos = new HashMap<>();

            if (!ventas.isEmpty()) {
                List<Long> ids = ventas.stream()
                        .map(Venta::getId)
                        .toList();

                List<Object[]> sumas = em.createQuery(
                        "SELECT p.venta.id, SUM(p.monto) FROM Pago p " +
                                "WHERE p.venta.id IN (:ids) " +
                                "GROUP BY p.venta.id",
                        Object[].class
                ).setParameter("ids", ids).getResultList();

                for (Object[] fila : sumas) {
                    pagos.put((Long) fila[0], (BigDecimal) fila[1]);
                }
            }

            List<Map<String, Object>> filas = new ArrayList<>();

            for (Venta v : ventas) {

                BigDecimal pagado = pagos.getOrDefault(
                        v.getId(), BigDecimal.ZERO
                );

                BigDecimal saldo = v.getTotal().subtract(pagado);

                String nombreCliente = v.getCliente() == null
                        ? "Público general"
                        : v.getCliente().getNombres() + " " +
                        v.getCliente().getApellidos();

                Map<String, Object> fila = new HashMap<>();
                fila.put("id", v.getId());
                fila.put("numero", v.getNumeroTicket());
                fila.put("cliente", nombreCliente);
                fila.put("total", v.getTotal());
                fila.put("pagado", pagado);
                fila.put("saldo", saldo);
                fila.put("estado", v.getEstadoPago());
                fila.put("fecha", v.getFecha() == null
                        ? "Sin fecha"
                        : v.getFecha().format(FORMATO));
                fila.put("pendiente", saldo.signum() > 0 &&
                        ("PENDIENTE".equals(v.getEstadoPago()) ||
                                "PARCIAL".equals(v.getEstadoPago())));
                fila.put("pagada",
                        "PAGADA".equals(v.getEstadoPago()));

                filas.add(fila);
            }

            request.setAttribute("ventas", filas);

        } catch (RuntimeException e) {
            throw new ServletException(
                    "No se pudo cargar el módulo de ventas.", e
            );
        }

        request.getRequestDispatcher(VISTA)
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        try {
            HttpSession session = request.getSession(false);

            if (session == null ||
                    !(session.getAttribute("usuarioId") instanceof Long)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            Long usuarioId = (Long) session.getAttribute("usuarioId");

            Long clienteId = leerId(request, "clienteId");
            Long atencionId = leerId(request, "atencionId");
            Long productoId = leerId(request, "productoId");

            Map<Long, Integer> productos = Map.of();

            String cantidadTexto = request.getParameter("cantidad");

            if (productoId != null) {
                if (cantidadTexto == null || cantidadTexto.isBlank()) {
                    throw new IllegalArgumentException(
                            "Indica la cantidad del producto."
                    );
                }

                int cantidad = Integer.parseInt(cantidadTexto);

                if (cantidad <= 0) {
                    throw new IllegalArgumentException(
                            "La cantidad debe ser mayor que cero."
                    );
                }

                productos = Map.of(productoId, cantidad);

            } else if (cantidadTexto != null &&
                    !cantidadTexto.isBlank()) {
                throw new IllegalArgumentException(
                        "Selecciona un producto para esa cantidad."
                );
            }

            Long ventaId = new VentaService(Jpa.factory()).crear(
                    usuarioId,
                    clienteId,
                    atencionId,
                    productos
            );

            session.setAttribute(
                    "flashExito",
                    "Venta #" + ventaId +
                            " creada. Ahora puedes registrar su pago simulado."
            );

        } catch (IllegalArgumentException | IllegalStateException e) {
            request.getSession().setAttribute(
                    "flashError", e.getMessage()
            );
        } catch (RuntimeException e) {
            throw new ServletException(
                    "No se pudo registrar la venta.", e
            );
        }

        response.sendRedirect(
                request.getContextPath() + "/recepcion/ventas"
        );
    }

    private Long leerId(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);

        if (valor == null || valor.isBlank()) {
            return null;
        }

        try {
            long id = Long.parseLong(valor);

            if (id <= 0) {
                throw new NumberFormatException();
            }

            return id;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "El campo " + nombre + " no es válido."
            );
        }
    }
}
