package com.pragma.plazoletaservice.infrastructure.input.controller;

import com.pragma.plazoletaservice.application.dto.DishDTO;
import com.pragma.plazoletaservice.application.dto.DishResponseDto;
import com.pragma.plazoletaservice.application.dto.PaginatedResponseDto;
import com.pragma.plazoletaservice.application.handler.ICategoryHandler;
import com.pragma.plazoletaservice.application.handler.IDishHandler;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping()
@AllArgsConstructor
public class DishController {

    private final IDishHandler dishHandler;
    private final ICategoryHandler categoryHandler;

    @PostMapping("/dishes")
    public ResponseEntity<Void> createDish(@Valid @RequestBody DishDTO dishDto) {
        dishHandler.createDish(dishDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PostMapping("/categories")
    public ResponseEntity<Void> createCategory(@RequestParam String categoryName) {
        categoryHandler.createCategory(categoryName);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/dishes")
    public ResponseEntity<Void> updateDish(@Valid @RequestBody DishDTO dishDto) {
        dishHandler.updateDish(dishDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/restaurants/{restaurantId}/dishes")
    public ResponseEntity<PaginatedResponseDto<DishResponseDto>> getDishesByRestaurant(
            @PathVariable Long restaurantId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(dishHandler.getDishesByRestaurant(restaurantId,categoryId, page, size));
    }

}
