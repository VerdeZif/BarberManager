package com.barbermanager.repository;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/** El servicio controla el EntityManager y la transacción. */
public abstract class RepositoryBase<T> {
    protected final EntityManager em;
    private final Class<T> tipo;

    protected RepositoryBase(EntityManager em, Class<T> tipo) {
        this.em = java.util.Objects.requireNonNull(em);
        this.tipo = tipo;
    }
    public Optional<T> buscarPorId(Long id) {
        return Optional.ofNullable(em.find(tipo, id));
    }
    public List<T> listarTodos() {
        return em.createQuery("select e from " + tipo.getSimpleName() + " e order by e.id", tipo).getResultList();
    }
    /** Inserta una entidad nueva dentro de la transacción del servicio. */
    public void guardar(T entidad) {
        em.persist(entidad);
    }
}
