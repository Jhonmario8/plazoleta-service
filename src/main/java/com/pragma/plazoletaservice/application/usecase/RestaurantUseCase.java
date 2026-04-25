package com.pragma.plazoletaservice.application.usecase;

import com.pragma.plazoletaservice.application.dto.PaginatedResponse;
import com.pragma.plazoletaservice.application.dto.RestaurantResponseDto;
import com.pragma.plazoletaservice.application.mapper.IRestaurantMapper;
import com.pragma.plazoletaservice.domain.api.IAuthenticationPort;
import com.pragma.plazoletaservice.domain.api.IRestaurantServicePort;
import com.pragma.plazoletaservice.domain.api.IUserServicePort;
import com.pragma.plazoletaservice.domain.constants.DomainConstants;
import com.pragma.plazoletaservice.domain.exception.ConflictException;
import com.pragma.plazoletaservice.domain.exception.NotFoundException;
import com.pragma.plazoletaservice.domain.exception.UnauthorizedException;
import com.pragma.plazoletaservice.domain.model.Employee;
import com.pragma.plazoletaservice.domain.model.Restaurant;
import com.pragma.plazoletaservice.domain.model.Role;
import com.pragma.plazoletaservice.domain.spi.IRestaurantPersistencePort;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RestaurantUseCase implements IRestaurantServicePort {

    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IAuthenticationPort authenticationPort;
    private final IUserServicePort userServicePort;
    private final IRestaurantMapper restaurantMapper;
    @Override
    public void createRestaurant(Restaurant restaurant) {

        Role ownerRole = userServicePort.getUserRole(restaurant.getOwnerId());
        if (ownerRole == null) {
            throw new NotFoundException(DomainConstants.MSG_OWNER_NOT_FOUND);
        }
        if (ownerRole != Role.OWNER) {
            throw new UnauthorizedException(DomainConstants.ONLY_OWNER_CAN_BE_ASSIGNED_MESSAGE);
        }
        if (restaurantPersistencePort.existsByNit(restaurant.getNit())) {
            throw new ConflictException(DomainConstants.MSG_NIT_ALREADY_EXISTS);
        }
        if (restaurantPersistencePort.existsByPhoneNumber(restaurant.getPhoneNumber())) {
            throw new ConflictException(DomainConstants.MSG_PHONE_NUMBER_ALREADY_EXISTS);
        }
        restaurantPersistencePort.saveRestaurant(restaurant);
    }

    @Override
    public void createEmployee(Employee employee) {

        Long currentUserId = authenticationPort.getCurrentUserId();

        Role ownerRole = authenticationPort.getCurrentUserRole();

        if (ownerRole != Role.OWNER) {
            throw new UnauthorizedException(DomainConstants.ONLY_OWNER_CAN_CREATE_EMPLOYEES);
        }
        Restaurant restaurant = restaurantPersistencePort.getRestaurantByOwnerId(currentUserId).orElseThrow(() -> new NotFoundException(DomainConstants.MSG_RESTAURANT_NOT_FOUND));

        employee.setRestaurantId(restaurant.getId());

        userServicePort.createEmployee(employee);
    }

    @Override
    public PaginatedResponse<RestaurantResponseDto> getRestaurants(int page, int size) {

        Page<Restaurant> restaurantPage = restaurantPersistencePort.getRestaurants(PageRequest.of(page, size));

        List<RestaurantResponseDto> content = restaurantPage.getContent()
                .stream()
                .map(restaurantMapper::toResponse)
                .toList();
        return new PaginatedResponse<>(
                content,
                restaurantPage.getNumber(),
                restaurantPage.getSize(),
                restaurantPage.getTotalElements(),
                restaurantPage.getTotalPages()
        );
    }
}