package com.pragma.plazoletaservice.infrastructure.output.jpa.adapter;

import com.pragma.plazoletaservice.domain.model.Category;
import com.pragma.plazoletaservice.domain.spi.ICategoryPersistencePort;
import com.pragma.plazoletaservice.infrastructure.output.jpa.mapper.ICategoryEntityMapper;
import com.pragma.plazoletaservice.infrastructure.output.jpa.repository.ICategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CategoryJpaAdapter implements ICategoryPersistencePort {

    private final ICategoryRepository categoryRepository;
    private final ICategoryEntityMapper categoryMapper;

    @Override
    public void saveCategory(Category category) {
        categoryRepository.save(categoryMapper.toEntity(category));
    }

    @Override
    public boolean existsCategoryByName(String name) {
        return categoryRepository.existsByName(name);
    }

    @Override
    public Optional<Category> findCategoryById(Long id) {
        return categoryRepository.findCategoryEntitiesById(id)
                .map(categoryMapper::toDomain);
    }
}
