package com.example.ispitni_projekat_f.dao;

import com.example.ispitni_projekat_f.model.entity.Category;
import jakarta.ejb.Stateless;

import java.util.List;

@Stateless
public class CategoryDAO extends SimpleAbstractDAO<Category, Long> {

    protected CategoryDAO() {
        super(Category.class);
    }

    public List<Category> getAll(int page, int size) {
        return em.createQuery("SELECT u FROM Category u ORDER BY u.id", Category.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }
}
