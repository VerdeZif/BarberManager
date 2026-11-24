package com.barbermanager.repository.ventas;

import com.barbermanager.model.Venta;
import com.barbermanager.repository.RepositoryBase;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public class VentaRepository extends RepositoryBase<Venta> {
    public VentaRepository(EntityManager em) {
        super(em, Venta.class);
    }

    /** Requiere una transacción activa. */
    public Optional<Venta> buscarPorIdConBloqueo(Long id) {
        return Optional.ofNullable(em.find(Venta.class, id, LockModeType.PESSIMISTIC_WRITE));
    }
}
