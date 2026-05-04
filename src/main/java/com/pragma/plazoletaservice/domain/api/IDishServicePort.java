package com.pragma.plazoletaservice.domain.api;

import com.pragma.plazoletaservice.application.dto.DishResponseDto;
import com.pragma.plazoletaservice.application.dto.PaginatedResponse;
import com.pragma.plazoletaservice.domain.model.Dish;

public interface IDishServicePort {
    void createDish(Dish dish,Long restaurantId, Long categoryId);
    void updateDish(Dish dish, Long restaurantId);
    PaginatedResponse<DishResponseDto> getDishes(Long restaurantId, Long categoryId, int page, int size);
}
