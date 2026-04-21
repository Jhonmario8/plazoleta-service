package com.pragma.plazoletaservice.application.handler;

import com.pragma.plazoletaservice.application.dto.RestaurantDto;
import com.pragma.plazoletaservice.application.mapper.IRestaurantMapper;
import com.pragma.plazoletaservice.domain.api.IRestaurantServicePort;
import com.pragma.plazoletaservice.domain.model.Role;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RestaurantHandler implements IRestaurantHandler{

    private final IRestaurantServicePort restaurantServicePort;
    private final IRestaurantMapper mapper;

    @Override
    public void createRestaurant(RestaurantDto dto, Role role) {
        restaurantServicePort.createRestaurant(mapper.toDomain(dto),  role);
    }

}
