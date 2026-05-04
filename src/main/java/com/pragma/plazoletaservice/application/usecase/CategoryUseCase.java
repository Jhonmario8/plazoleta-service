package com.pragma.plazoletaservice.application.usecase;

import com.pragma.plazoletaservice.application.constants.ApplicationConstants;
import com.pragma.plazoletaservice.domain.api.ICategoryServicePort;
import com.pragma.plazoletaservice.domain.model.Category;
import com.pragma.plazoletaservice.domain.spi.ICategoryPersistencePort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CategoryUseCase implements ICategoryServicePort {

    private final ICategoryPersistencePort categoryPersistencePort;

    @Override
    public void createCategory(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(ApplicationConstants.MSG_CATEGORY_NAME_REQUIRED);
        }
        if (categoryPersistencePort.existsCategoryByName(name)) {
            throw new IllegalArgumentException(ApplicationConstants.MSG_CATEGORY_ALREADY_EXISTS);
        }
        categoryPersistencePort.saveCategory(new Category(null, name));
    }
}
