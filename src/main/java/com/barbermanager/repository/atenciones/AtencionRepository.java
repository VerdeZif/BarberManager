package com.barbermanager.repository.atenciones;

import com.barbermanager.model.Atencion;
import com.barbermanager.repository.RepositoryBase;
import jakarta.persistence.EntityManager;
import java.util.Optional;

public class AtencionRepository extends RepositoryBase<Atencion> {
    public AtencionRepository(EntityManager em) {
        super(em, Atencion.class);
    }

    public Optional<Atencion> buscarPorCita(Long id) {
        return em.createQuery("select e from Atencion e where e.cita.id = :id", Atencion.class)
                .setParameter("id", id).getResultStream().findFirst();
    }
}
