package com.pragma.plazoletaservice.domain.spi;

import com.pragma.plazoletaservice.domain.model.Order;

public interface IOrderPersistencePort {

    void saveOrder(Order order);
    boolean existsActiveOrderByClientId(Long clientId);
}
