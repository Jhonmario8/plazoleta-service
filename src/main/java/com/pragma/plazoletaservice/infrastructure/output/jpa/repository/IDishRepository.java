package com.pragma.plazoletaservice.infrastructure.output.jpa.repository;

import com.pragma.plazoletaservice.infrastructure.output.jpa.entites.DishEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDishRepository extends JpaRepository<DishEntity, Long> {
    boolean existsByNameAndRestaurantEntityId(String name, Long restaurantEntityId);
    Page<DishEntity> findByRestaurantEntityId(Long restaurantId, Pageable pageable);
    Page<DishEntity> findByRestaurantEntityIdAndCategory_Id (Long restaurantId, Long categoryId, Pageable pageable);
}
