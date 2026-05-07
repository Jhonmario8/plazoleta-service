package com.pragma.plazoletaservice.application.handler;

import com.pragma.plazoletaservice.application.dto.EmployeeRequestDTO;
import com.pragma.plazoletaservice.application.dto.PaginatedResponseDto;
import com.pragma.plazoletaservice.application.dto.RestaurantDTO;
import com.pragma.plazoletaservice.application.dto.RestaurantResponseDto;



public interface IRestaurantHandler {
        void createRestaurant(RestaurantDTO dto);
        void createEmployee(EmployeeRequestDTO dto);
        PaginatedResponseDto<RestaurantResponseDto> getRestaurants(int page, int size);
}
