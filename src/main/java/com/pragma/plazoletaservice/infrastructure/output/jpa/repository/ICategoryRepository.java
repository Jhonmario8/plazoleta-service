package com.pragma.plazoletaservice.infrastructure.output.jpa.repository;

import com.pragma.plazoletaservice.infrastructure.output.jpa.entites.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ICategoryRepository extends JpaRepository<CategoryEntity, Long> {
     boolean existsByName(String name);
     Optional<CategoryEntity> findCategoryEntitiesById(Long id);
}
