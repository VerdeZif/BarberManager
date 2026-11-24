package com.barbermanager.repository.inventario;

import com.barbermanager.model.Producto;
import com.barbermanager.repository.RepositoryBase;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public class ProductoRepository extends RepositoryBase<Producto> {
    public ProductoRepository(EntityManager em) {
        super(em, Producto.class);
    }

    /** Requiere una transacción activa. */
    public Optional<Producto> buscarPorIdConBloqueo(Long id) {
        return Optional.ofNullable(em.find(Producto.class, id, LockModeType.PESSIMISTIC_WRITE));
    }
}
