package com.pragma.plazoletaservice.domain.api;


import com.pragma.plazoletaservice.application.dto.OrderDto;
import com.pragma.plazoletaservice.application.dto.PaginatedResponseDto;
import com.pragma.plazoletaservice.domain.model.Order;
import com.pragma.plazoletaservice.domain.model.OrderStatus;

public interface IOrderServicePort {
    void createOrder(Order order);

    PaginatedResponseDto<OrderDto> getOrders(Long restaurantId, OrderStatus status, int page, int size);

    void assignEmployeeToOrder(Long orderId, Long employeeId);

    void updateOrderStatus(Long orderId, OrderStatus status);
}
