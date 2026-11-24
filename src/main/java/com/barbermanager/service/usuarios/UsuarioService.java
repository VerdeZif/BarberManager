package com.barbermanager.service.usuarios;

import com.barbermanager.model.Rol;
import com.barbermanager.model.Usuario;
import com.barbermanager.service.Transacciones;
import jakarta.persistence.EntityManagerFactory;

public class UsuarioService {
    private final Transacciones transacciones;
    public UsuarioService(EntityManagerFactory factory) { transacciones = new Transacciones(factory); }
    public void cambiarEstado(Long id, boolean activo) {
        transacciones.ejecutar(em -> {
            Usuario usuario = em.find(Usuario.class, id);
            if (usuario == null) throw new IllegalArgumentException("Usuario no encontrado");
            usuario.setActivo(activo);
            return null;
        });
    }
    public void cambiarRol(Long id, Rol rol) {
        if (rol == null) throw new IllegalArgumentException("Rol inválido");
        transacciones.ejecutar(em -> {
            Usuario usuario = em.find(Usuario.class, id);
            if (usuario == null) throw new IllegalArgumentException("Usuario no encontrado");
            usuario.setRol(rol);
            return null;
        });
    }
}
