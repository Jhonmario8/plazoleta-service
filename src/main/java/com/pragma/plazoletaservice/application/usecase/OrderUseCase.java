package com.pragma.plazoletaservice.application.usecase;

import com.pragma.plazoletaservice.application.dto.OrderDto;
import com.pragma.plazoletaservice.application.dto.PaginatedResponseDto;
import com.pragma.plazoletaservice.application.mapper.IOrderMapper;
import com.pragma.plazoletaservice.domain.api.IAuthenticationPort;
import com.pragma.plazoletaservice.domain.api.IOrderServicePort;
import com.pragma.plazoletaservice.domain.api.IUserServicePort;
import com.pragma.plazoletaservice.domain.constants.DomainConstants;
import com.pragma.plazoletaservice.domain.exception.DomainException;
import com.pragma.plazoletaservice.domain.exception.UnauthorizedException;
import com.pragma.plazoletaservice.domain.model.*;
import com.pragma.plazoletaservice.domain.spi.IDishPersistencePort;
import com.pragma.plazoletaservice.domain.spi.IOrderPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderUseCase implements IOrderServicePort {

    private final IOrderPersistencePort orderPersistencePort;
    private final IAuthenticationPort authenticationPort;
    private final IDishPersistencePort dishPersistencePort;
    private final IUserServicePort userServicePort;
    private final IOrderMapper mapper;
    @Override
    public void createOrder(Order order) {

        Long clientId = authenticationPort.getCurrentUserId();
        validateClientHasNoActiveOrders(clientId);
        validateDishesBelongToRestaurant(order);

        order.setClientId(clientId);
        order.setStatus(OrderStatus.PENDING);
        order.setDate(LocalDateTime.now());
        orderPersistencePort.saveOrder(order);
    }

    @Override
    public PaginatedResponseDto<OrderDto> getOrders(Long restaurantId, OrderStatus status, int page, int size) {
        Long userId = authenticationPort.getCurrentUserId();
        Role userRole = userServicePort.getUserRole(userId);
        if (userRole != Role.EMPLOYEE) {
            throw new UnauthorizedException(DomainConstants.MSG_ONLY_EMPLOYEE_CAN_GET_ORDERS);
        }
        Page<Order> ordersPage = orderPersistencePort.getOrders(restaurantId, status, page, size);
        List<OrderDto> orderDos = ordersPage.getContent().stream()
                .map(mapper::toDto)
                .toList();
        return new PaginatedResponseDto<>(
                orderDos,
                ordersPage.getNumber(),
                ordersPage.getSize(),
                ordersPage.getTotalElements(),
                ordersPage.getTotalPages()
        );
    }



    private void validateClientHasNoActiveOrders(Long clientId) {
        if (orderPersistencePort.existsActiveOrderByClientId(clientId)) {
            throw new DomainException(DomainConstants.MSG_CLIENT_HAS_ACTIVE_ORDER);
        }
    }

    private void validateDishesBelongToRestaurant(Order order) {
        Long restaurantId = order.getRestaurantId();
        List<Long> dishIds = order.getDishes().stream().map(OrderDish::getDishId).toList();
        List<Dish> dishes = dishPersistencePort.getDishesByIds(dishIds, restaurantId);
        if (dishes.size() != dishIds.size()) {
            throw new DomainException(DomainConstants.MSG_SOME_DISHES_NOT_FOUND_IN_RESTAURANT);
        }
    }
}
