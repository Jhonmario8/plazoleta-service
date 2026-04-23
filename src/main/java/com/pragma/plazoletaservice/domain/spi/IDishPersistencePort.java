package com.pragma.plazoletaservice.domain.spi;

import com.pragma.plazoletaservice.domain.model.Dish;

import java.util.Optional;

public interface IDishPersistencePort {
    void saveDish(Dish dish);
    boolean existsDishByNameAndRestaurantId(String name, Long restaurantId);
    Optional<Dish> getDishById(Long id);
}
