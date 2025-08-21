package com.example.ispitni_projekat_f.model.dto;

import com.example.ispitni_projekat_f.model.entity.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryDTO {
    private long id;
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Description is required")
    private String description;

    public CategoryDTO(long id) {
        this.id = id;
    }

    public static CategoryDTO fromEntity(Category category) {
        if (category == null) return null;
        CategoryDTO dto = new CategoryDTO();
        dto.id = category.getId();
        dto.name = category.getName();
        dto.description = category.getDescription();
        return dto;
    }

    public Category toEntity() {
        Category entity = new Category();
        entity.setId(this.id);
        entity.setName(this.name);
        entity.setDescription(this.description);
        return entity;
    }
}

