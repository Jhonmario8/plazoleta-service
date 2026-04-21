package com.pragma.plazoletaservice.application.usecase;

import com.pragma.plazoletaservice.domain.api.IRestaurantServicePort;
import com.pragma.plazoletaservice.domain.api.IUserServicePort;
import com.pragma.plazoletaservice.domain.constants.DomainConstants;
import com.pragma.plazoletaservice.domain.exception.NotFoundException;
import com.pragma.plazoletaservice.domain.exception.UnauthorizedException;
import com.pragma.plazoletaservice.domain.model.Restaurant;
import com.pragma.plazoletaservice.domain.model.Role;
import com.pragma.plazoletaservice.domain.spi.IRestaurantPersistencePort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RestaurantUseCase implements IRestaurantServicePort {

    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IUserServicePort userServicePort;

    @Override
    public void createRestaurant(Restaurant restaurant) {

        Role ownerRole = userServicePort.getUserRole(restaurant.getOwnerId());
        if (ownerRole == null) {
            throw new NotFoundException(DomainConstants.MSG_OWNER_NOT_FOUND);
        }
        if (ownerRole != Role.OWNER) {
            throw new UnauthorizedException(DomainConstants.ONLY_OWNER_CAN_BE_ASSIGNED_MESSAGE);
        }
        restaurantPersistencePort.saveRestaurant(restaurant);
    }
}