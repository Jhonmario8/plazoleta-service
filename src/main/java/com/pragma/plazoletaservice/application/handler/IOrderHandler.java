package com.pragma.plazoletaservice.application.handler;

import com.pragma.plazoletaservice.application.dto.OrderDto;

public interface IOrderHandler {
        void createOrder(OrderDto orderDto);
}
