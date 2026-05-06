package com.pragma.plazoletaservice.domain.model;

import com.pragma.plazoletaservice.domain.constants.DomainConstants;
import com.pragma.plazoletaservice.domain.exception.DomainException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Order {

    private Long id;
    private Long restaurantId;
    private Long clientId;
    private LocalDateTime date;
    private OrderStatus status;
    private List<OrderDish> dishes;

    public Order(Long id, Long restaurantId, Long clientId, LocalDateTime date, OrderStatus status, List<OrderDish> dishes) {

        validateOrderHasDishes(dishes);
        validateRestaurantIdNotNull(restaurantId);
        this.id = id;
        this.restaurantId = restaurantId;
        this.clientId = clientId;
        this.date = date;
        this.status = status;
        this.dishes = dishes;
    }

    private void validateOrderHasDishes(List<OrderDish> dishes) {
        if (dishes == null || dishes.isEmpty()) {
            throw new DomainException(DomainConstants.MSG_ORDER_MUST_AT_LEAST_ONE_DISH);
        }
    }

    private void validateRestaurantIdNotNull(Long restaurantId) {
        if (restaurantId == null) {
            throw new DomainException(DomainConstants.MSG_ORDER_RESTAURANT_ID_CANNOT_BE_NULL);
        }
    }
}
