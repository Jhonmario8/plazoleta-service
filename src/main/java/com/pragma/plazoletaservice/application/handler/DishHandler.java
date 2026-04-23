package com.pragma.plazoletaservice.application.handler;

import com.pragma.plazoletaservice.application.dto.DishDTO;
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
        dishServicePort.createDish(mapper.toDomain(dto),dto.getRestaurantId());
    }

    @Override
    public void updateDish(DishDTO dto) {
        dishServicePort.updateDish(mapper.toDomain(dto),dto.getRestaurantId());
    }
}
