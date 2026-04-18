package com.pragma.plazoletaservice.domain.spi;

import com.pragma.plazoletaservice.domain.model.Restaurant;

public interface IRestaurantPersistencePort {
    void saveRestaurant(Restaurant restaurant);
}
