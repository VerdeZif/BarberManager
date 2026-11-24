package com.barbermanager.controller.clientes;

import com.barbermanager.controller.ControllerBase;
import com.barbermanager.service.clientes.ClienteService;
import com.barbermanager.util.Jpa;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/recepcion/clientes")
public class ClienteServlet extends ControllerBase {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            new ClienteService(Jpa.factory()).crear(request.getParameter("nombres"), request.getParameter("apellidos"), request.getParameter("telefono"));
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (RuntimeException e) {
            error(response, e);
        }
    }
}
