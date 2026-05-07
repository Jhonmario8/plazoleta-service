package com.pragma.plazoletaservice.application.handler;

import com.pragma.plazoletaservice.application.dto.OrderDto;
import com.pragma.plazoletaservice.application.dto.PaginatedResponseDto;
import com.pragma.plazoletaservice.application.mapper.IOrderMapper;
import com.pragma.plazoletaservice.domain.api.IOrderServicePort;
import com.pragma.plazoletaservice.domain.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderHandler implements IOrderHandler{

    private final IOrderServicePort orderServicePort;
    private final IOrderMapper mapper;

    @Override
    public void createOrder(OrderDto orderDto) {
        orderServicePort.createOrder(mapper.toDomain(orderDto));
    }

    @Override
    public PaginatedResponseDto<OrderDto> getOrders(Long restaurantId, OrderStatus status, int page, int size) {
        return orderServicePort.getOrders(restaurantId, status, page, size);
    }

    @Override
    public void assignEmployeeToOrder(Long orderId, Long employeeId) {
        orderServicePort.assignEmployeeToOrder(orderId, employeeId);
    }

    @Override
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        orderServicePort.updateOrderStatus(orderId, status);
    }
}
