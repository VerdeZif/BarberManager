package com.barbermanager.controller.ventas;

import com.barbermanager.controller.ControllerBase;
import com.barbermanager.service.ventas.PagoService;
import com.barbermanager.util.Jpa;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/recepcion/ventas/pago")
public class PagoServlet extends ControllerBase {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (request.getParameter("monto") == null) throw new IllegalArgumentException("Falta monto");
            new PagoService(Jpa.factory()).registrarPago(id(request, "ventaId", true), request.getParameter("metodo"), new BigDecimal(request.getParameter("monto")));
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (RuntimeException e) {
            error(response, e);
        }
    }
}
