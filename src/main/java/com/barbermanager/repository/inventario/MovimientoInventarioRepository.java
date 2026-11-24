package com.barbermanager.repository.inventario;

import com.barbermanager.model.MovimientoInventario;
import com.barbermanager.repository.RepositoryBase;
import jakarta.persistence.EntityManager;
import java.util.List;

public class MovimientoInventarioRepository extends RepositoryBase<MovimientoInventario> {
    public MovimientoInventarioRepository(EntityManager em) {
        super(em, MovimientoInventario.class);
    }

    public List<MovimientoInventario> listarPorProducto(Long id) {
        return em.createQuery("select e from MovimientoInventario e where e.producto.id = :id", MovimientoInventario.class)
                .setParameter("id", id).getResultList();
    }
}
