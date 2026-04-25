package com.pragma.plazoletaservice.domain.spi;

import com.pragma.plazoletaservice.domain.model.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IRestaurantPersistencePort {
    void saveRestaurant(Restaurant restaurant);
    Optional<Restaurant> getRestaurantById(Long id);
    boolean existsByNit(String nit);
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<Restaurant> getRestaurantByOwnerId(Long ownerId);
    Page<Restaurant> getRestaurants(Pageable pageable);
}
