package com.pragma.plazoletaservice.infrastructure.input.controller;


import com.pragma.plazoletaservice.application.dto.RestaurantDto;
import com.pragma.plazoletaservice.application.handler.IRestaurantHandler;
import com.pragma.plazoletaservice.infrastructure.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restaurants")
@AllArgsConstructor
public class RestaurantController {

    private final IRestaurantHandler restaurantHandler;

         @PostMapping
        public ResponseEntity<Void> createRestaurant(@Valid @RequestBody RestaurantDto restaurantDto) {
            restaurantHandler.createRestaurant(restaurantDto, SecurityUtils.getCurrentUserRole(), SecurityUtils.getCurrentUserId());
            return new ResponseEntity<>(HttpStatus.CREATED);
        }


}
