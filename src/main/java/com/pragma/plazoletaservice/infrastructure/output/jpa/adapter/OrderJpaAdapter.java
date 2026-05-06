package com.pragma.plazoletaservice.infrastructure.output.jpa.adapter;

import com.pragma.plazoletaservice.domain.model.Order;
import com.pragma.plazoletaservice.domain.spi.IOrderPersistencePort;
import com.pragma.plazoletaservice.infrastructure.output.jpa.entites.OrderEntity;
import com.pragma.plazoletaservice.infrastructure.output.jpa.mapper.IOrderEntityMapper;
import com.pragma.plazoletaservice.infrastructure.output.jpa.repository.IOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderJpaAdapter implements IOrderPersistencePort {

    private final IOrderEntityMapper orderMapper;
    private final IOrderRepository orderRepository;

    @Override
    public void saveOrder(Order order) {

        OrderEntity entity = orderMapper.toEntity(order);
        System.out.println("Cantidadae de platos en dominio: " + order.getDishes().size());
        System.out.println("Platos despues de mapear a entidad: " + entity.getOrderDishes().size());
        if (entity.getOrderDishes() != null) {
            entity.getOrderDishes().forEach(orderDishEntity -> orderDishEntity.setOrder(entity));
        }
        orderRepository.save(entity);
    }
    @Override
    public boolean existsActiveOrderByClientId(Long clientId) {
        return orderRepository.existsByClientId(clientId);
    }

}
