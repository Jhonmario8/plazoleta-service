package com.pragma.plazoletaservice.infrastructure.input.controller;

import com.pragma.plazoletaservice.application.dto.OrderDto;
import com.pragma.plazoletaservice.application.handler.IOrderHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
