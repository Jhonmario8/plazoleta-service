package com.pragma.plazoletaservice.application.handler;

import com.pragma.plazoletaservice.application.dto.EmployeeRequestDTO;
import com.pragma.plazoletaservice.application.dto.RestaurantDTO;
import com.pragma.plazoletaservice.application.dto.RestaurantResponseDto;
import org.springframework.data.domain.Page;


public interface IRestaurantHandler {
        void createRestaurant(RestaurantDTO dto);
        void createEmployee(EmployeeRequestDTO dto);
        Page<RestaurantResponseDto> getRestaurants(int page, int size);
}
