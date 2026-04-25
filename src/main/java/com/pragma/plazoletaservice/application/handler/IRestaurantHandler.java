package com.pragma.plazoletaservice.application.handler;

import com.pragma.plazoletaservice.application.dto.EmployeeRequestDTO;
import com.pragma.plazoletaservice.application.dto.PaginatedResponse;
import com.pragma.plazoletaservice.application.dto.RestaurantDTO;
import com.pragma.plazoletaservice.application.dto.RestaurantResponseDto;



public interface IRestaurantHandler {
        void createRestaurant(RestaurantDTO dto);
        void createEmployee(EmployeeRequestDTO dto);
        PaginatedResponse<RestaurantResponseDto> getRestaurants(int page, int size);
}
