package com.pragma.plazoletaservice.infrastructure.output.jpa.adapter;

import com.pragma.plazoletaservice.domain.model.Order;
import com.pragma.plazoletaservice.domain.model.OrderStatus;
import com.pragma.plazoletaservice.domain.spi.IOrderPersistencePort;
import com.pragma.plazoletaservice.infrastructure.output.jpa.entites.OrderEntity;
import com.pragma.plazoletaservice.infrastructure.output.jpa.mapper.IOrderEntityMapper;
import com.pragma.plazoletaservice.infrastructure.output.jpa.repository.IOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderJpaAdapter implements IOrderPersistencePort {

    private final IOrderEntityMapper orderMapper;
    private final IOrderRepository orderRepository;

    @Override
    public void saveOrder(Order order) {

        OrderEntity entity = orderMapper.toEntity(order);
        if (entity.getOrderDishes() != null) {
            entity.getOrderDishes().forEach(orderDishEntity -> orderDishEntity.setOrder(entity));
        }
        orderRepository.save(entity);
    }
    @Override
    public boolean existsActiveOrderByClientId(Long clientId) {
        return orderRepository.existsByClientId(clientId);
    }

    @Override
    public Page<Order> getOrders(Long restaurantId, OrderStatus status, int page, int size) {
        Page<OrderEntity> orderEntities = orderRepository.findAllByRestaurantIdAndStatus(restaurantId, status, PageRequest.of(page, size));
        return orderEntities.map(orderMapper::toDomain);
    }
}
