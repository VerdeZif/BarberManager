package com.barbermanager.service;

import java.math.BigDecimal;

public final class Validaciones {
    private Validaciones() { }
    public static String texto(String valor, int maximo, String campo) {
        if (valor == null || valor.isBlank() || valor.trim().length() > maximo)
            throw new IllegalArgumentException(campo + " inválido");
        return valor.trim();
    }
    public static void importe(BigDecimal valor, int precision, String campo) {
        if (valor == null || valor.signum() < 0 || valor.scale() > 2 || valor.precision() > precision)
            throw new IllegalArgumentException(campo + " inválido");
    }
}
