package com.pragma.plazoletaservice.domain.api;

import com.pragma.plazoletaservice.domain.model.Restaurant;


public interface IRestaurantServicePort {
    void createRestaurant(Restaurant restaurant);
}
