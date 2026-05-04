package com.pragma.plazoletaservice.domain.spi;


import com.pragma.plazoletaservice.domain.model.Category;

import java.util.Optional;

public interface ICategoryPersistencePort {
    void saveCategory(Category category);
    boolean existsCategoryByName(String name);
    Optional<Category> findCategoryById(Long id);
}
