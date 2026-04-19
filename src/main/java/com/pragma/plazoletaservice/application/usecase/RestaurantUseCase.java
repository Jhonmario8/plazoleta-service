package com.pragma.plazoletaservice.application.usecase;

import com.pragma.plazoletaservice.domain.api.IRestaurantServicePort;
import com.pragma.plazoletaservice.domain.model.Restaurant;
import com.pragma.plazoletaservice.domain.spi.IRestaurantPersistencePort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RestaurantUseCase implements IRestaurantServicePort {

    private final IRestaurantPersistencePort restaurantPersistencePort;


    @Override
    public void createRestaurant(Restaurant restaurant) {
        restaurantPersistencePort.saveRestaurant(restaurant);
    }
}
