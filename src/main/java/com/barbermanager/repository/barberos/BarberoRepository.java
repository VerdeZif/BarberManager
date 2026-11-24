package com.barbermanager.repository.barberos;

import com.barbermanager.model.Barbero;
import com.barbermanager.repository.RepositoryBase;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public class BarberoRepository extends RepositoryBase<Barbero> {
    public BarberoRepository(EntityManager em) {
        super(em, Barbero.class);
    }

    /** Requiere una transacción activa. */
    public Optional<Barbero> buscarPorIdConBloqueo(Long id) {
        return Optional.ofNullable(em.find(Barbero.class, id, LockModeType.PESSIMISTIC_WRITE));
    }
}
