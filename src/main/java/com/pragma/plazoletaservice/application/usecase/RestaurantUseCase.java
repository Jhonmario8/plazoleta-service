package com.pragma.plazoletaservice.application.usecase;

import com.pragma.plazoletaservice.domain.api.IRestaurantServicePort;
import com.pragma.plazoletaservice.domain.constants.DomainConstants;
import com.pragma.plazoletaservice.domain.exception.DomainException;
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

    @Override
    public void createRestaurant(Restaurant restaurant, Role role, Long ownerId) {
        if (ownerId == null || ownerId <= 0) {
            throw new DomainException(DomainConstants.MSG_OWNER_ID_INVALID);
        }

        if (role != Role.ADMIN) {
            throw new UnauthorizedException(DomainConstants.ONLY_ADMIN_CAN_CREATE_RESTAURANT_MESSAGE);
        }
        restaurant.setOwnerId(ownerId);
        restaurantPersistencePort.saveRestaurant(restaurant);
    }
}
