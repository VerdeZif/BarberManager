package com.barbermanager.controller;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class ControllerBase extends HttpServlet {
    protected Long id(HttpServletRequest request, String nombre, boolean obligatorio) {
        String valor = request.getParameter(nombre);
        if (valor == null || valor.isBlank()) {
            if (obligatorio) throw new IllegalArgumentException("Falta " + nombre);
            return null;
        }
        try {
            long numero = Long.parseLong(valor);
            if (numero <= 0) throw new NumberFormatException();
            return numero;
        } catch (NumberFormatException e) { throw new IllegalArgumentException(nombre + " inválido"); }
    }

    protected void error(HttpServletResponse response, RuntimeException e) throws IOException {
        if (e instanceof IllegalArgumentException) response.sendError(400, e.getMessage());
        else if (e instanceof IllegalStateException) response.sendError(409, e.getMessage());
        else if (e instanceof SecurityException) response.sendError(403);
        else { log("Error al procesar la solicitud", e); response.sendError(500); }
    }
}
