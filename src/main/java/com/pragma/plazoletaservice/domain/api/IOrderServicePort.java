package com.pragma.plazoletaservice.domain.api;


import com.pragma.plazoletaservice.domain.model.Order;

public interface IOrderServicePort {
    void createOrder(Order order);
}
