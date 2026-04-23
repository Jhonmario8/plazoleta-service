package com.pragma.plazoletaservice.domain.api;

import com.pragma.plazoletaservice.domain.model.Dish;

public interface IDishServicePort {
    void createDish(Dish dish,Long restaurantId);
    void updateDish(Dish dish, Long restaurantId);
}
