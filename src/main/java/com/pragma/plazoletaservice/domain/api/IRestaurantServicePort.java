package com.pragma.plazoletaservice.domain.api;

import com.pragma.plazoletaservice.domain.model.Restaurant;
import com.pragma.plazoletaservice.domain.model.Role;

public interface IRestaurantServicePort {
    void createRestaurant(Restaurant restaurant, Role role, Long ownerId);
}
