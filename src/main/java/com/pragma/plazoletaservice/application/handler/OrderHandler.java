package com.pragma.plazoletaservice.application.handler;

import com.pragma.plazoletaservice.application.dto.OrderDto;
import com.pragma.plazoletaservice.application.mapper.IOrderMapper;
import com.pragma.plazoletaservice.domain.api.IOrderServicePort;
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
}
