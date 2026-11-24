package com.barbermanager.service.usuarios;

import com.barbermanager.model.Cliente;
import com.barbermanager.model.Rol;
import com.barbermanager.model.Usuario;
import com.barbermanager.repository.usuarios.UsuarioRepository;
import com.barbermanager.repository.clientes.ClienteRepository;
import com.barbermanager.util.Password;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.Optional;

public class AutenticacionService {
    private final EntityManagerFactory factory;
    public AutenticacionService(EntityManagerFactory factory) { this.factory = factory; }

    public void registrar(String username, String email, char[] password, String nombres, String apellidos, String telefono) {
        username = texto(username, 50, "Usuario");
        email = texto(email, 120, "Correo");
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) throw new IllegalArgumentException("Correo inválido");
        nombres = texto(nombres, 100, "Nombres");
        apellidos = texto(apellidos, 100, "Apellidos");
        if (password == null || password.length < 12 || password.length > 128) throw new IllegalArgumentException("La contraseña debe tener entre 12 y 128 caracteres");
        if (telefono != null && telefono.trim().length() > 20) throw new IllegalArgumentException("Teléfono demasiado largo");
        String hash = Password.hash(password);
        EntityManager em = factory.createEntityManager();
        try {
            em.getTransaction().begin();
            UsuarioRepository usuarios = new UsuarioRepository(em);
            if (usuarios.buscarPorUsername(username).isPresent() || usuarios.existeEmail(email)) throw new IllegalArgumentException("Usuario o correo ya registrado");
            Usuario usuario = new Usuario(username, email, hash, Rol.CLIENTE);
            usuarios.guardar(usuario);
            new ClienteRepository(em).guardar(new Cliente(usuario, nombres, apellidos, telefono == null || telefono.isBlank() ? null : telefono.trim()));
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally { em.close(); }
    }

    public Optional<Usuario> autenticar(String username, char[] password) {
        if (username == null || password == null || username.length() > 50 || password.length > 128) return Optional.empty();
        EntityManager em = factory.createEntityManager();
        try { return new UsuarioRepository(em).buscarPorUsername(username.trim()).filter(u -> u.isActivo() && Password.verificar(password, u.getPasswordHash())); }
        finally { em.close(); }
    }

    private static String texto(String valor, int max, String campo) {
        if (valor == null || valor.isBlank() || valor.trim().length() > max) throw new IllegalArgumentException(campo + " inválido");
        return valor.trim();
    }
}
