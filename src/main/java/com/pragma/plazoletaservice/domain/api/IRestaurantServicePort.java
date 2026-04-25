package com.pragma.plazoletaservice.domain.api;


import com.pragma.plazoletaservice.domain.model.Employee;
import com.pragma.plazoletaservice.domain.model.Restaurant;
import org.springframework.data.domain.Page;


public interface IRestaurantServicePort {
    void createRestaurant(Restaurant restaurant);
    void createEmployee(Employee employee);
    Page<Restaurant> getRestaurants(int page, int size);
}
