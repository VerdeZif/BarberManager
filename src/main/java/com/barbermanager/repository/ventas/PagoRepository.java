package com.barbermanager.repository.ventas;

import com.barbermanager.model.Pago;
import com.barbermanager.repository.RepositoryBase;
import jakarta.persistence.EntityManager;
import java.util.List;

public class PagoRepository extends RepositoryBase<Pago> {
    public PagoRepository(EntityManager em) {
        super(em, Pago.class);
    }

    public List<Pago> listarPorVenta(Long id) {
        return em.createQuery("select e from Pago e where e.venta.id = :id", Pago.class)
                .setParameter("id", id).getResultList();
    }
}
