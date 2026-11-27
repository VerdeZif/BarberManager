
package com.barbermanager.controller.ventas;

import com.barbermanager.model.Venta;
import com.barbermanager.service.ventas.TicketService;
import com.barbermanager.util.Jpa;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {
        "/mis-tickets",
        "/mis-tickets/ver"
})
public class MisTicketsServlet extends HttpServlet {

    private static final String VISTA_LISTADO =
            "/WEB-INF/views/publico/mis-tickets.jsp";

    private static final String VISTA_TICKET =
            "/WEB-INF/views/panel/ticket.jsp";

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        // Solo el cliente autenticado puede ingresar.
        if (!"CLIENTE".equals(
                request.getAttribute("rolActual"))) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN
            );
            return;
        }

        HttpSession session = request.getSession(false);

        if (session == null ||
                !(session.getAttribute("usuarioId")
                        instanceof Long)) {

            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        Long usuarioId =
                (Long) session.getAttribute("usuarioId");

        try {
            Long clienteId = obtenerClienteId(usuarioId);

            if ("/mis-tickets/ver".equals(
                    request.getServletPath())) {

                mostrarTicket(
                        request,
                        response,
                        clienteId
                );

            } else {
                listarTickets(
                        request,
                        response,
                        clienteId
                );
            }

        } catch (RuntimeException e) {
            throw new ServletException(
                    "Error al consultar los tickets.",
                    e
            );
        }
    }

    private Long obtenerClienteId(Long usuarioId) {

        EntityManager em = Jpa.factory().createEntityManager();

        try {
            List<Long> ids = em.createQuery(
                            "SELECT c.id FROM Cliente c " +
                                    "WHERE c.usuario.id = :usuarioId",
                            Long.class
                    )
                    .setParameter("usuarioId", usuarioId)
                    .setMaxResults(1)
                    .getResultList();

            return ids.isEmpty() ? null : ids.get(0);

        } finally {
            em.close();
        }
    }

    private void listarTickets(
            HttpServletRequest request,
            HttpServletResponse response,
            Long clienteId
    ) throws ServletException, IOException {

        List<Map<String, Object>> tickets =
                new ArrayList<>();

        if (clienteId == null) {
            request.setAttribute(
                    "mensajeInfo",
                    "Tu cuenta todavía no tiene una " +
                            "ficha de cliente vinculada."
            );

        } else {

            EntityManager em =
                    Jpa.factory().createEntityManager();

            try {
                List<Venta> ventas = em.createQuery(
                                "SELECT v FROM Venta v " +
                                        "WHERE v.cliente.id = :clienteId " +
                                        "AND v.estadoPago = 'PAGADA' " +
                                        "ORDER BY v.fecha DESC, v.id DESC",
                                Venta.class
                        )
                        .setParameter("clienteId", clienteId)
                        .setMaxResults(100)
                        .getResultList();

                for (Venta venta : ventas) {

                    Map<String, Object> fila =
                            new HashMap<>();

                    fila.put("id", venta.getId());

                    fila.put(
                            "numero",
                            venta.getNumeroTicket()
                    );

                    fila.put(
                            "fecha",
                            venta.getFecha() == null
                                    ? "Sin fecha"
                                    : venta.getFecha().format(FORMATO)
                    );

                    fila.put("total", venta.getTotal());

                    fila.put(
                            "estado",
                            venta.getEstadoPago()
                    );

                    tickets.add(fila);
                }

            } finally {
                em.close();
            }
        }

        request.setAttribute("tickets", tickets);

        request.getRequestDispatcher(VISTA_LISTADO)
                .forward(request, response);
    }

    private void mostrarTicket(
            HttpServletRequest request,
            HttpServletResponse response,
            Long clienteId
    ) throws ServletException, IOException {

        if (clienteId == null) {
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "Ticket no disponible."
            );
            return;
        }

        try {
            String parametro = request.getParameter("id");

            long ventaId = Long.parseLong(parametro);

            if (ventaId <= 0) {
                throw new IllegalArgumentException(
                        "ID inválido."
                );
            }

            // TicketService verifica:
            // 1. Que la venta exista.
            // 2. Que esté totalmente pagada.
            // 3. Que pertenezca al cliente autenticado.
            TicketService.Ticket ticket =
                    new TicketService(Jpa.factory())
                            .obtener(
                                    ventaId,
                                    clienteId,
                                    false
                            );

            request.setAttribute(
                    "numeroTicket",
                    ticket.numero()
            );

            request.setAttribute(
                    "fechaTicket",
                    ticket.fecha() == null
                            ? "Sin fecha"
                            : ticket.fecha().format(FORMATO)
            );

            request.setAttribute(
                    "totalTicket",
                    ticket.total()
            );

            request.setAttribute(
                    "itemsTicket",
                    ticket.items().stream()
                            .map(item -> Map.of(
                                    "descripcion",
                                    item.descripcion(),
                                    "cantidad",
                                    item.cantidad(),
                                    "precio",
                                    item.precio(),
                                    "subtotal",
                                    item.subtotal()
                            ))
                            .toList()
            );

            request.setAttribute(
                    "pagosTicket",
                    ticket.pagos().stream()
                            .map(pago -> Map.of(
                                    "metodo", pago.metodo(),
                                    "monto", pago.monto()
                            ))
                            .toList()
            );

            request.setAttribute(
                    "urlVolverTicket",
                    request.getContextPath() + "/mis-tickets"
            );

            request.getRequestDispatcher(VISTA_TICKET)
                    .forward(request, response);

        } catch (IllegalArgumentException | SecurityException e) {

            // No revelar si un ID pertenece a otro cliente.
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "Ticket no disponible."
            );
        }
    }
}
