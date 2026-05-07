package com.pragma.plazoletaservice.infrastructure.input.controller;

import com.pragma.plazoletaservice.application.dto.OrderDto;
import com.pragma.plazoletaservice.application.dto.PaginatedResponseDto;
import com.pragma.plazoletaservice.application.handler.IOrderHandler;
import com.pragma.plazoletaservice.domain.model.OrderStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final IOrderHandler orderHandler;


    @PostMapping
    public ResponseEntity<Void> createOrder(@Valid @RequestBody OrderDto orderDto) {
        orderHandler.createOrder(orderDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<PaginatedResponseDto<OrderDto>> getOrders(
            @PathVariable Long restaurantId,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponseDto<OrderDto> response = orderHandler.getOrders(restaurantId, status, page, size);
        return ResponseEntity.ok(response);
    }

}
