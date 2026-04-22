package com.pragma.plazoletaservice.domain.spi;

import com.pragma.plazoletaservice.domain.model.Restaurant;

import java.util.Optional;

public interface IRestaurantPersistencePort {
    void saveRestaurant(Restaurant restaurant);
    Optional<Restaurant> getRestaurantById(Long id);
}
