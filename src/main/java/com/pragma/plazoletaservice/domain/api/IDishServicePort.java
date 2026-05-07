package com.pragma.plazoletaservice.domain.api;

import com.pragma.plazoletaservice.application.dto.DishResponseDto;
import com.pragma.plazoletaservice.application.dto.PaginatedResponseDto;
import com.pragma.plazoletaservice.domain.model.Dish;

public interface IDishServicePort {
    void createDish(Dish dish,Long restaurantId, Long categoryId);
    void updateDish(Dish dish, Long restaurantId);
    PaginatedResponseDto<DishResponseDto> getDishes(Long restaurantId, Long categoryId, int page, int size);
}
