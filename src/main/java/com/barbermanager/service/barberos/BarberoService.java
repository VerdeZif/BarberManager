package com.barbermanager.service.barberos;

import com.barbermanager.model.Barbero;
import com.barbermanager.service.Transacciones;
import com.barbermanager.service.Validaciones;
import jakarta.persistence.EntityManagerFactory;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Arrays;

public class BarberoService {
    private final Transacciones transacciones;
    public BarberoService(EntityManagerFactory factory) { transacciones = new Transacciones(factory); }
    public Long guardar(Long id, String nombres, String apellidos, String dias, LocalTime inicio,
                        LocalTime fin, BigDecimal porcentaje, boolean activo) {
        String nombre = Validaciones.texto(nombres, 100, "Nombres");
        String apellido = Validaciones.texto(apellidos, 100, "Apellidos");
        String jornada = Validaciones.texto(dias, 20, "Días laborales");
        if (!jornada.matches("[1-7](,[1-7])*") || Arrays.stream(jornada.split(",")).distinct().count() != jornada.split(",").length)
            throw new IllegalArgumentException("Días laborales inválidos");
        if (inicio == null || fin == null || !fin.isAfter(inicio)) throw new IllegalArgumentException("Horario inválido");
        Validaciones.importe(porcentaje, 5, "Porcentaje");
        if (porcentaje.compareTo(new BigDecimal("100")) > 0) throw new IllegalArgumentException("Porcentaje inválido");
        return transacciones.ejecutar(em -> {
            Barbero barbero = id == null ? new Barbero() : em.find(Barbero.class, id);
            if (barbero == null) throw new IllegalArgumentException("Barbero no encontrado");
            barbero.setNombres(nombre); barbero.setApellidos(apellido); barbero.setDiasLaborables(jornada);
            barbero.setHoraInicio(inicio); barbero.setHoraFin(fin); barbero.setPorcentajeComision(porcentaje);
            barbero.setActivo(activo);
            if (id == null) { em.persist(barbero); em.flush(); }
            return barbero.getId();
        });
    }
}
