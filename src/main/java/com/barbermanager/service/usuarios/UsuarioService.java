
package com.barbermanager.service.usuarios;

import com.barbermanager.model.Rol;
import com.barbermanager.model.Usuario;
import com.barbermanager.service.Transacciones;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;

import java.util.List;

public class UsuarioService {

    private final Transacciones transacciones;

    public UsuarioService(EntityManagerFactory factory) {
        this.transacciones = new Transacciones(factory);
    }

    public void cambiarEstado(Long id, boolean activo) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "ID de usuario inválido."
            );
        }

        transacciones.ejecutar(em -> {

            // Bloquea las cuentas administradoras activas
            // mientras se comprueba esta operación.
            List<Usuario> administradores =
                    em.createQuery(
                                    "SELECT u FROM Usuario u " +
                                            "WHERE u.rol = :rol AND u.activo = true " +
                                            "ORDER BY u.id",
                                    Usuario.class
                            )
                            .setParameter("rol", Rol.ADMINISTRADOR)
                            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                            .getResultList();

            Usuario usuario = em.find(Usuario.class, id);

            if (usuario == null) {
                throw new IllegalArgumentException(
                        "Usuario no encontrado."
                );
            }

            if (!activo
                    && usuario.isActivo()
                    && usuario.getRol() == Rol.ADMINISTRADOR
                    && administradores.size() <= 1) {

                throw new IllegalStateException(
                        "No puedes desactivar al último " +
                                "administrador activo."
                );
            }

            usuario.setActivo(activo);

            return null;
        });
    }

    public void cambiarRol(Long id, Rol rol) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "ID de usuario inválido."
            );
        }

        if (rol == null) {
            throw new IllegalArgumentException(
                    "Rol inválido."
            );
        }

        transacciones.ejecutar(em -> {

            List<Usuario> administradores =
                    em.createQuery(
                                    "SELECT u FROM Usuario u " +
                                            "WHERE u.rol = :rol AND u.activo = true " +
                                            "ORDER BY u.id",
                                    Usuario.class
                            )
                            .setParameter("rol", Rol.ADMINISTRADOR)
                            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                            .getResultList();

            Usuario usuario = em.find(Usuario.class, id);

            if (usuario == null) {
                throw new IllegalArgumentException(
                        "Usuario no encontrado."
                );
            }

            if (usuario.isActivo()
                    && usuario.getRol() == Rol.ADMINISTRADOR
                    && rol != Rol.ADMINISTRADOR
                    && administradores.size() <= 1) {

                throw new IllegalStateException(
                        "No puedes cambiar el rol del " +
                                "último administrador activo."
                );
            }

            usuario.setRol(rol);

            return null;
        });
    }
}
