package com.barbermanager.repository.usuarios;

import com.barbermanager.model.Usuario;
import com.barbermanager.repository.RepositoryBase;
import jakarta.persistence.EntityManager;
import java.util.Optional;

public class UsuarioRepository extends RepositoryBase<Usuario> {
    public UsuarioRepository(EntityManager em) { super(em, Usuario.class); }
    public Optional<Usuario> buscarPorUsername(String username) {
        return em.createQuery("select u from Usuario u where u.username = :username", Usuario.class)
                .setParameter("username", username).getResultStream().findFirst();
    }
    public boolean existeEmail(String email) {
        return em.createQuery("select count(u) from Usuario u where u.email = :email", Long.class)
                .setParameter("email", email).getSingleResult() > 0;
    }
}
