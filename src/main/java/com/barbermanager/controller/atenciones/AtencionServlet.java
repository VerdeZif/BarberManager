package com.barbermanager.controller.atenciones;

import com.barbermanager.controller.ControllerBase;
import com.barbermanager.service.atenciones.AtencionService;
import com.barbermanager.util.Jpa;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/recepcion/atenciones")
public class AtencionServlet extends ControllerBase {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            AtencionService servicio = new AtencionService(Jpa.factory());
            if ("iniciar".equals(request.getParameter("accion"))) servicio.iniciar(id(request, "citaId", true));
            else if ("finalizar".equals(request.getParameter("accion"))) servicio.finalizar(id(request, "citaId", true), request.getParameter("observaciones"));
            else throw new IllegalArgumentException("Acción inválida");
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (RuntimeException e) {
            error(response, e);
        }
    }
}
