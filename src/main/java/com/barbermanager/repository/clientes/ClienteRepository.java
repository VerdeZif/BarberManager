package com.barbermanager.repository.clientes;

import com.barbermanager.model.Cliente;
import com.barbermanager.repository.RepositoryBase;
import jakarta.persistence.EntityManager;
import java.util.Optional;

public class ClienteRepository extends RepositoryBase<Cliente> {
    public ClienteRepository(EntityManager em) { super(em, Cliente.class); }

    public Optional<Cliente> buscarPorUsuario(Long id) {
        return em.createQuery("select c from Cliente c where c.usuario.id = :id", Cliente.class)
                .setParameter("id", id).getResultStream().findFirst();
    }
}
