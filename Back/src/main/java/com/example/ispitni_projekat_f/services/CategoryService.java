package com.example.ispitni_projekat_f.services;

import com.example.ispitni_projekat_f.dao.CategoryDAO;
import com.example.ispitni_projekat_f.model.dto.CategoryDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class CategoryService {

    @Inject
    private CategoryDAO categoryDAO;

    public List<CategoryDTO> getCategories(int page, int size) {
        return categoryDAO.getAll(page, size).stream().map(CategoryDTO::fromEntity).toList();
    }

    public CategoryDTO createCategory(CategoryDTO category) {
        return CategoryDTO.fromEntity(categoryDAO.insert(category.toEntity()));
    }

    public CategoryDTO updateCategory(Long id, CategoryDTO category) {
        category.setId(id);
        return CategoryDTO.fromEntity(categoryDAO.update(category.toEntity()));
    }

    public CategoryDTO deleteCategory(Long id) {
        return CategoryDTO.fromEntity(categoryDAO.delete(id));
    }
}
