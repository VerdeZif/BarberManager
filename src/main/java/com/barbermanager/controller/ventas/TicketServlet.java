
package com.barbermanager.controller.ventas;

import com.barbermanager.service.ventas.TicketService;
import com.barbermanager.util.Jpa;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@WebServlet("/recepcion/ventas/ticket")
public class TicketServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String valorId = request.getParameter("id");
            long ventaId = Long.parseLong(valorId);

            if (ventaId <= 0) {
                throw new IllegalArgumentException("ID inválido");
            }

            // Esta ruta está protegida por AutorizacionFilter:
            // solo ADMINISTRADOR y RECEPCIONISTA.
            TicketService.Ticket ticket =
                    new TicketService(Jpa.factory()).obtener(
                            ventaId, null, true
                    );

            request.setAttribute("numeroTicket", ticket.numero());

            request.setAttribute(
                    "fechaTicket",
                    ticket.fecha() == null
                            ? "Sin fecha"
                            : ticket.fecha().format(
                            DateTimeFormatter.ofPattern(
                                    "dd/MM/yyyy HH:mm"
                            )
                    )
            );

            request.setAttribute("totalTicket", ticket.total());

            request.setAttribute(
                    "itemsTicket",
                    ticket.items().stream()
                            .map(i -> Map.of(
                                    "descripcion", i.descripcion(),
                                    "cantidad", i.cantidad(),
                                    "precio", i.precio(),
                                    "subtotal", i.subtotal()
                            ))
                            .toList()
            );

            request.setAttribute(
                    "pagosTicket",
                    ticket.pagos().stream()
                            .map(p -> Map.of(
                                    "metodo", p.metodo(),
                                    "monto", p.monto()
                            ))
                            .toList()
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/panel/ticket.jsp"
            ).forward(request, response);

        } catch (IllegalArgumentException e) {
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "Ticket no disponible."
            );
        } catch (SecurityException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        } catch (RuntimeException e) {
            throw new ServletException(
                    "No se pudo obtener el ticket.", e
            );
        }
    }
}
