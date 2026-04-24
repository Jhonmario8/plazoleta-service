package com.pragma.plazoletaservice.domain.api;


import com.pragma.plazoletaservice.domain.model.Employee;
import com.pragma.plazoletaservice.domain.model.Restaurant;


public interface IRestaurantServicePort {
    void createRestaurant(Restaurant restaurant);
    void createEmployee(Employee employee);
}
