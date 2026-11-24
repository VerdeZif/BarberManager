package com.barbermanager.repository.comisiones;

import com.barbermanager.model.Comision;
import com.barbermanager.repository.RepositoryBase;
import jakarta.persistence.EntityManager;
import java.util.Optional;

public class ComisionRepository extends RepositoryBase<Comision> {
    public ComisionRepository(EntityManager em) {
        super(em, Comision.class);
    }

    public Optional<Comision> buscarPorAtencion(Long id) {
        return em.createQuery("select e from Comision e where e.atencion.id = :id", Comision.class)
                .setParameter("id", id).getResultStream().findFirst();
    }
}
