package com.barbermanager.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.function.Function;

/** Abre una transacción por operación y garantiza su cierre. */
public final class Transacciones {
    private final EntityManagerFactory factory;

    public Transacciones(EntityManagerFactory factory) {
        this.factory = java.util.Objects.requireNonNull(factory);
    }

    public <T> T ejecutar(Function<EntityManager, T> operacion) {
        EntityManager em = factory.createEntityManager();
        try {
            em.getTransaction().begin();
            T resultado = operacion.apply(em);
            em.getTransaction().commit();
            return resultado;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
