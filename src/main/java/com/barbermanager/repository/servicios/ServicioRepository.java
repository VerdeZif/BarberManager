package com.barbermanager.repository.servicios;

import com.barbermanager.model.Servicio;
import com.barbermanager.repository.RepositoryBase;
import jakarta.persistence.EntityManager;

public class ServicioRepository extends RepositoryBase<Servicio> {
    public ServicioRepository(EntityManager em) {
        super(em, Servicio.class);
    }
}
