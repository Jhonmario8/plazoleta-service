package com.pragma.plazoletaservice.application.handler;

import com.pragma.plazoletaservice.application.dto.DishDTO;
import com.pragma.plazoletaservice.application.dto.DishResponseDto;
import com.pragma.plazoletaservice.application.dto.PaginatedResponseDto;
import com.pragma.plazoletaservice.application.mapper.IDishMapper;
import com.pragma.plazoletaservice.domain.api.IDishServicePort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DishHandler implements IDishHandler {

    private final IDishServicePort dishServicePort;
    private final IDishMapper mapper;

    @Override
    public void createDish(DishDTO dto) {
        dishServicePort.createDish(mapper.toDomain(dto),dto.getRestaurantId(),dto.getCategoryId());
    }

    @Override
    public void updateDish(DishDTO dto) {
        dishServicePort.updateDish(mapper.toDomain(dto),dto.getRestaurantId());
    }
    @Override
    public PaginatedResponseDto<DishResponseDto> getDishesByRestaurant(Long restaurantId, Long categoryId, int page, int size) {
        return dishServicePort.getDishes(restaurantId, categoryId, page, size);
    }
}
