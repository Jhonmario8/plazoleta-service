package com.pragma.plazoletaservice.application.usecase;

import com.pragma.plazoletaservice.domain.api.IAuthenticationPort;
import com.pragma.plazoletaservice.domain.api.IDishServicePort;
import com.pragma.plazoletaservice.domain.constants.DomainConstants;
import com.pragma.plazoletaservice.domain.exception.ConflictException;
import com.pragma.plazoletaservice.domain.exception.NotFoundException;
import com.pragma.plazoletaservice.domain.exception.UnauthorizedException;
import com.pragma.plazoletaservice.domain.model.Dish;
import com.pragma.plazoletaservice.domain.model.Restaurant;
import com.pragma.plazoletaservice.domain.spi.IDishPersistencePort;
import com.pragma.plazoletaservice.domain.spi.IRestaurantPersistencePort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DishUseCase implements IDishServicePort {

    private final IDishPersistencePort dishPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IAuthenticationPort authenticationPort;

    @Override
    public void createDish(Dish dish, Long restaurantId) {


        Restaurant restaurant = restaurantPersistencePort.getRestaurantById(restaurantId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.MSG_RESTAURANT_NOT_FOUND));

        if (dishPersistencePort.existsDishByNameAndRestaurantId(dish.getName(), restaurantId)) {
            throw new ConflictException(DomainConstants.MSG_DISH_ALREADY_EXISTS);
        }
        Long ownerId = authenticationPort.getCurrentUserId();

        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new UnauthorizedException(DomainConstants.MSG_NOT_RESTAURANT_OWNER);
        }
        dish.setRestaurant(restaurant);
        dishPersistencePort.saveDish(dish);
    }

    @Override
    public void updateDish(Dish dish, Long restaurantId) {

        Restaurant restaurant = restaurantPersistencePort.getRestaurantById(restaurantId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.MSG_RESTAURANT_NOT_FOUND));

        Dish existingDish = dishPersistencePort.getDishById(dish.getId())
                .orElseThrow(() -> new NotFoundException(DomainConstants.MSG_DISH_NOT_FOUND));

        Long ownerId = authenticationPort.getCurrentUserId();

        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new UnauthorizedException(DomainConstants.MSG_NOT_RESTAURANT_OWNER);
        }
        if (!existingDish.getRestaurant().getId().equals(restaurantId)) {
            throw new ConflictException(DomainConstants.MSG_DISH_RESTAURANT_MISMATCH);
        }

        existingDish.setName(dish.getName());
        existingDish.setDescription(dish.getDescription());
        existingDish.setPrice(dish.getPrice());
        existingDish.setCategory(dish.getCategory());
        existingDish.setActive(dish.getActive());

        dishPersistencePort.saveDish(existingDish);
    }
}
