package com.example.ispitni_projekat_f.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public abstract class SimpleAbstractDAO<E, K> {
    private final Class<E> entityClass;
    @PersistenceContext(unitName = "myPU")
    protected EntityManager em;

    protected SimpleAbstractDAO(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    public E insert(E entity) {
        em.persist(entity);
        return entity;
    }

    public E update(E entity) {
        return em.merge(entity);
    }

    public E delete(K id) {
        E entity = em.find(entityClass, id);
        if (entity != null) em.remove(entity);
        return entity;
    }

    public E findById(K id) {
        return em.find(entityClass, id);
    }
}

