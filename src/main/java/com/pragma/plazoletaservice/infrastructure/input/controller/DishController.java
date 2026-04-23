package com.pragma.plazoletaservice.infrastructure.input.controller;

import com.pragma.plazoletaservice.application.dto.DishDTO;
import com.pragma.plazoletaservice.application.handler.IDishHandler;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dishes")
@AllArgsConstructor
public class DishController {

    private final IDishHandler dishHandler;

    @PostMapping
    public ResponseEntity<Void> createDish(@Valid @RequestBody DishDTO dishDto) {
        dishHandler.createDish(dishDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    public ResponseEntity<Void> updateDish(@Valid @RequestBody DishDTO dishDto) {
        dishHandler.updateDish(dishDto);
        return ResponseEntity.ok().build();
    }

}
