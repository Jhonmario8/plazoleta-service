package com.pragma.plazoletaservice.application.handler;

import com.pragma.plazoletaservice.application.dto.RestaurantDto;
import com.pragma.plazoletaservice.domain.model.Role;

public interface IRestaurantHandler {
        void createRestaurant(RestaurantDto dto, Role role);
}
