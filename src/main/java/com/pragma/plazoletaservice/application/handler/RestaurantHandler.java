package com.pragma.plazoletaservice.application.handler;

import com.pragma.plazoletaservice.application.dto.EmployeeRequestDTO;
import com.pragma.plazoletaservice.application.dto.PaginatedResponse;
import com.pragma.plazoletaservice.application.dto.RestaurantDTO;
import com.pragma.plazoletaservice.application.dto.RestaurantResponseDto;
import com.pragma.plazoletaservice.application.mapper.IEmployeeMapper;
import com.pragma.plazoletaservice.application.mapper.IRestaurantMapper;
import com.pragma.plazoletaservice.domain.api.IRestaurantServicePort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RestaurantHandler implements IRestaurantHandler{

    private final IRestaurantServicePort restaurantServicePort;
    private final IRestaurantMapper mapper;
    private final IEmployeeMapper employeeMapper;

    @Override
    public void createRestaurant(RestaurantDTO dto) {
        restaurantServicePort.createRestaurant(mapper.toDomain(dto));
    }

    @Override
    public void createEmployee(EmployeeRequestDTO dto) {
        restaurantServicePort.createEmployee(employeeMapper.toDomain(dto));
    }

    @Override
    public PaginatedResponse<RestaurantResponseDto> getRestaurants(int page, int size) {
        return restaurantServicePort.getRestaurants(page, size);
    }
}
