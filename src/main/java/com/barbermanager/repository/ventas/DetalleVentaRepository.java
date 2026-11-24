package com.barbermanager.repository.ventas;

import com.barbermanager.model.DetalleVenta;
import com.barbermanager.repository.RepositoryBase;
import jakarta.persistence.EntityManager;
import java.util.List;

public class DetalleVentaRepository extends RepositoryBase<DetalleVenta> {
    public DetalleVentaRepository(EntityManager em) {
        super(em, DetalleVenta.class);
    }

    public List<DetalleVenta> listarPorVenta(Long id) {
        return em.createQuery("select e from DetalleVenta e where e.venta.id = :id", DetalleVenta.class)
                .setParameter("id", id).getResultList();
    }
}
