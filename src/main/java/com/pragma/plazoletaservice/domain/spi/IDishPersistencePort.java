package com.pragma.plazoletaservice.domain.spi;

import com.pragma.plazoletaservice.domain.model.Dish;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface IDishPersistencePort {
    void saveDish(Dish dish);
    boolean existsDishByNameAndRestaurantId(String name, Long restaurantId);
    Optional<Dish> getDishById(Long id);
    Page<Dish> getDishes(Long restaurantId, Long categoryId, int page, int size);
}
