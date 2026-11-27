
package com.barbermanager.controller.ventas;

import com.barbermanager.controller.ControllerBase;
import com.barbermanager.service.ventas.PagoService;
import com.barbermanager.util.Jpa;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/recepcion/ventas/pago")
public class PagoServlet extends ControllerBase {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Long ventaId = id(request, "ventaId", true);

            String montoTexto = request.getParameter("monto");

            if (montoTexto == null || montoTexto.isBlank()) {
                throw new IllegalArgumentException(
                        "Debes ingresar un monto."
                );
            }

            BigDecimal monto = new BigDecimal(montoTexto);

            new PagoService(Jpa.factory()).registrarPago(
                    ventaId,
                    request.getParameter("metodo"),
                    monto
            );

            request.getSession().setAttribute(
                    "flashExito",
                    "Pago simulado registrado correctamente."
            );

        } catch (IllegalArgumentException | IllegalStateException e) {
            request.getSession().setAttribute(
                    "flashError", e.getMessage()
            );
        } catch (RuntimeException e) {
            throw new ServletException(
                    "No se pudo registrar el pago.", e
            );
        }

        response.sendRedirect(
                request.getContextPath() + "/recepcion/ventas"
        );
    }
}
