package com.barbermanager.service.clientes;

import com.barbermanager.model.Cliente;
import com.barbermanager.service.Transacciones;
import com.barbermanager.service.Validaciones;
import jakarta.persistence.EntityManagerFactory;

public class ClienteService {
    private final Transacciones transacciones;
    public ClienteService(EntityManagerFactory factory) { transacciones = new Transacciones(factory); }

    public Long crear(String nombres, String apellidos, String telefono) {
        String nombre = Validaciones.texto(nombres, 100, "Nombres");
        String apellido = Validaciones.texto(apellidos, 100, "Apellidos");
        String contacto = telefono(telefono);
        return transacciones.ejecutar(em -> {
            Cliente cliente = new Cliente(null, nombre, apellido, contacto);
            em.persist(cliente);
            em.flush();
            return cliente.getId();
        });
    }
    public void actualizar(Long id, String nombres, String apellidos, String telefono) {
        String nombre = Validaciones.texto(nombres, 100, "Nombres");
        String apellido = Validaciones.texto(apellidos, 100, "Apellidos");
        String contacto = telefono(telefono);
        transacciones.ejecutar(em -> {
            Cliente cliente = em.find(Cliente.class, id);
            if (cliente == null) throw new IllegalArgumentException("Cliente no encontrado");
            cliente.setNombres(nombre); cliente.setApellidos(apellido); cliente.setTelefono(contacto);
            return null;
        });
    }
    public void cambiarEstado(Long id, boolean activo) {
        transacciones.ejecutar(em -> {
            Cliente cliente = em.find(Cliente.class, id);
            if (cliente == null) throw new IllegalArgumentException("Cliente no encontrado");
            cliente.setActivo(activo);
            return null;
        });
    }
    private String telefono(String valor) {
        return valor == null || valor.isBlank() ? null : Validaciones.texto(valor, 20, "Teléfono");
    }
}
