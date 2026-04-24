package com.pragma.plazoletaservice.infrastructure.input.controller;


import com.pragma.plazoletaservice.application.dto.EmployeeRequestDTO;
import com.pragma.plazoletaservice.application.dto.RestaurantDTO;
import com.pragma.plazoletaservice.application.handler.IRestaurantHandler;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
@AllArgsConstructor
public class RestaurantController {

    private final IRestaurantHandler restaurantHandler;

    @PostMapping("/restaurants")
    public ResponseEntity<Void> createRestaurant(@Valid @RequestBody RestaurantDTO restaurantDto) {
        restaurantHandler.createRestaurant(restaurantDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/users/employee")
    public ResponseEntity<Void> createEmployee(@Valid @RequestBody EmployeeRequestDTO employeeRequestDto) {
        restaurantHandler.createEmployee(employeeRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }


}
