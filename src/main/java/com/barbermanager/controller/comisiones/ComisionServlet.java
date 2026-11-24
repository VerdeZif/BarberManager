package com.barbermanager.controller.comisiones;

import com.barbermanager.controller.ControllerBase;
import com.barbermanager.service.comisiones.ComisionService;
import com.barbermanager.util.Jpa;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/comisiones")
public class ComisionServlet extends ControllerBase {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            new ComisionService(Jpa.factory()).marcarPagada(id(request, "id", true));
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (RuntimeException e) {
            error(response, e);
        }
    }
}
