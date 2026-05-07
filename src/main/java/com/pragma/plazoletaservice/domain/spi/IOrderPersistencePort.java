package com.pragma.plazoletaservice.domain.spi;

import com.pragma.plazoletaservice.domain.model.Order;
import com.pragma.plazoletaservice.domain.model.OrderStatus;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface IOrderPersistencePort {

    void saveOrder(Order order);
    boolean existsActiveOrderByClientId(Long clientId);
    Page<Order> getOrders(Long restaurantId, OrderStatus status, int page, int size);
    Optional<Order> getOrderById(Long orderId);


}
