package com.pragma.plazoletaservice.application.handler;

import com.pragma.plazoletaservice.application.dto.DishDTO;
import com.pragma.plazoletaservice.application.dto.DishResponseDto;
import com.pragma.plazoletaservice.application.dto.PaginatedResponseDto;

public interface IDishHandler {
    void createDish(DishDTO dishDTO);
    void updateDish(DishDTO dishDTO);
    PaginatedResponseDto<DishResponseDto> getDishesByRestaurant(Long restaurantId, Long categoryId, int page, int size);
}
