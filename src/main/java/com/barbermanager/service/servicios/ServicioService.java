package com.barbermanager.service.servicios;

import com.barbermanager.model.Servicio;
import com.barbermanager.service.Transacciones;
import com.barbermanager.service.Validaciones;
import jakarta.persistence.EntityManagerFactory;
import java.math.BigDecimal;

public class ServicioService {
    private final Transacciones transacciones;
    public ServicioService(EntityManagerFactory factory) { transacciones = new Transacciones(factory); }
    public Long guardar(Long id, String nombre, String descripcion, BigDecimal precio, int duracion, boolean activo) {
        String nombreValido = Validaciones.texto(nombre, 100, "Nombre");
        Validaciones.importe(precio, 10, "Precio");
        if (duracion <= 0) throw new IllegalArgumentException("Duración inválida");
        return transacciones.ejecutar(em -> {
            Servicio servicio = id == null ? new Servicio() : em.find(Servicio.class, id);
            if (servicio == null) throw new IllegalArgumentException("Servicio no encontrado");
            servicio.setNombre(nombreValido); servicio.setDescripcion(descripcion);
            servicio.setPrecio(precio); servicio.setDuracionMinutos(duracion); servicio.setActivo(activo);
            if (id == null) { em.persist(servicio); em.flush(); }
            return servicio.getId();
        });
    }
}
