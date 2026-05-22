package com.pragma.plazoletaservice.application.usecase;

import com.pragma.plazoletaservice.application.dto.OrderDto;
import com.pragma.plazoletaservice.application.dto.PaginatedResponseDto;
import com.pragma.plazoletaservice.application.mapper.IOrderMapper;
import com.pragma.plazoletaservice.domain.api.*;
import com.pragma.plazoletaservice.domain.constants.DomainConstants;
import com.pragma.plazoletaservice.domain.exception.DomainException;
import com.pragma.plazoletaservice.domain.exception.NotFoundException;
import com.pragma.plazoletaservice.domain.exception.UnauthorizedException;
import com.pragma.plazoletaservice.domain.model.*;
import com.pragma.plazoletaservice.domain.spi.IDishPersistencePort;
import com.pragma.plazoletaservice.domain.spi.IOrderPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class OrderUseCase implements IOrderServicePort {

    private final IOrderPersistencePort orderPersistencePort;
    private final IAuthenticationPort authenticationPort;
    private final IDishPersistencePort dishPersistencePort;
    private final IUserServicePort userServicePort;
    private final IOrderMapper mapper;
    private final ISmsServicePort smsServicePort;
    private final ITraceabilityServicePort traceabilityServicePort;

    @Override
    public void createOrder(Order order) {

        Long clientId = authenticationPort.getCurrentUserId();
        validateClientHasNoActiveOrders(clientId);
        validateDishesBelongToRestaurant(order);
        createTraceabilityRecord(order.getId(), clientId, null, null, OrderStatus.PENDING.name(), LocalDateTime.now());
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

    @Override
    public void assignEmployeeToOrder(Long orderId, Long employeeId) {
        Order order = orderPersistencePort.getOrderById(orderId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.MSG_ORDER_NOT_FOUND));

        validateOrderIsAssignable(order);
        validateEmployeeIdIsEmpty(order);
        validateEmployeeFromSameRestaurant(employeeId, order.getRestaurantId());
        createTraceabilityRecord(orderId, order.getClientId(), employeeId, order.getStatus().name(), OrderStatus.IN_PREPARATION.name(), LocalDateTime.now());
        order.setEmployeeId(employeeId);
        order.setStatus(OrderStatus.IN_PREPARATION);
        orderPersistencePort.saveOrder(order);
    }

    @Override
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderPersistencePort.getOrderById(orderId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.MSG_ORDER_NOT_FOUND));

        Employee client = userServicePort.getUserById(order.getClientId())
                .orElseThrow(() -> new NotFoundException(DomainConstants.MSG_CLIENT_NOT_FOUND));

        validateStatus(status, order.getStatus());

        OrderTraceabilityRequest orderTraceabilityRequest = traceabilityServicePort.findTraceabilityById(orderId);
        orderTraceabilityRequest.setPreviousState(orderTraceabilityRequest.getNewState());
        orderTraceabilityRequest.setNewState(status.name());
        traceabilityServicePort.saveTraceabilityRecord(orderTraceabilityRequest);
        String phoneNumber = "whatsapp:+57" + client.getPhoneNumber();
        if (status == OrderStatus.READY) {
            Random random = new Random();
            int orderCode = random.nextInt(9000) + 1000;
            smsServicePort.sendSms(new Sms(phoneNumber, DomainConstants.MSG_SMS_ORDER_READY + orderCode));
            order.setOrderCode(orderCode);
        }
        order.setStatus(status);
        orderPersistencePort.saveOrder(order);
    }

    @Override
    public void cancelOrder(Long orderId) {
        Order order = orderPersistencePort.getOrderById(orderId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.MSG_ORDER_NOT_FOUND));

        Employee client = userServicePort.getUserById(order.getClientId())
                .orElseThrow(() -> new NotFoundException(DomainConstants.MSG_CLIENT_NOT_FOUND));

        String phoneNumber = "whatsapp:+57" + client.getPhoneNumber();

        if (order.getStatus() != OrderStatus.PENDING) {
            smsServicePort.sendSms(new Sms(phoneNumber, DomainConstants.MSG_ONLY_PENDING_ORDERS_CAN_BE_CANCELLED));
            throw new DomainException(DomainConstants.MSG_ONLY_PENDING_ORDERS_CAN_BE_CANCELLED);
        }


        OrderTraceabilityRequest orderTraceabilityRequest = traceabilityServicePort.findTraceabilityById(orderId);
        orderTraceabilityRequest.setPreviousState(orderTraceabilityRequest.getNewState());
        orderTraceabilityRequest.setNewState(OrderStatus.CANCELLED.name());
        orderTraceabilityRequest.setEndTime(LocalDateTime.now());
        traceabilityServicePort.saveTraceabilityRecord(orderTraceabilityRequest);
        smsServicePort.sendSms(new Sms(phoneNumber, DomainConstants.MSG_SMS_ORDER_CANCELLED));
        order.setStatus(OrderStatus.CANCELLED);
        orderPersistencePort.saveOrder(order);
    }

    @Override
    public void deliverOrder(Long orderId, Integer orderCode) {
        Order order = orderPersistencePort.getOrderById(orderId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.MSG_ORDER_NOT_FOUND));

        Employee client = userServicePort.getUserById(order.getClientId())
                .orElseThrow(() -> new NotFoundException(DomainConstants.MSG_CLIENT_NOT_FOUND));

        String phoneNumber = "whatsapp:+57" + client.getPhoneNumber();

        if (order.getStatus() != OrderStatus.READY) {
            throw new DomainException(DomainConstants.MSG_ONLY_READY_ORDERS_CAN_BE_DELIVERED);
        }

        if (!orderCode.equals(order.getOrderCode())) {
            throw new DomainException(DomainConstants.MSG_INVALID_ORDER_CODE);
        }

        OrderTraceabilityRequest orderTraceabilityRequest = traceabilityServicePort.findTraceabilityById(orderId);
        orderTraceabilityRequest.setPreviousState(orderTraceabilityRequest.getNewState());
        orderTraceabilityRequest.setNewState(OrderStatus.DELIVERED.name());
        orderTraceabilityRequest.setEndTime(LocalDateTime.now());
        traceabilityServicePort.saveTraceabilityRecord(orderTraceabilityRequest);

        smsServicePort.sendSms(new Sms(phoneNumber, DomainConstants.MSG_SMS_ORDER_DELIVERED));
        order.setStatus(OrderStatus.DELIVERED);
        orderPersistencePort.saveOrder(order);
    }

    private void createTraceabilityRecord(Long orderId, Long clientId, Long employeeId, String previousState, String newState, LocalDateTime startTime) {
        OrderTraceabilityRequest traceability = new OrderTraceabilityRequest();
        traceability.setOrderId(orderId);
        traceability.setClientId(clientId);
        traceability.setEmployeeId(employeeId);
        traceability.setPreviousState(previousState);
        traceability.setNewState(newState);
        traceability.setStartTime(startTime);
        traceabilityServicePort.saveTraceabilityRecord(traceability);
    }

    private void validateStatus(OrderStatus requestStatus, OrderStatus orderStatus ) {

        if (requestStatus == OrderStatus.IN_PREPARATION && orderStatus != OrderStatus.PENDING) {
            throw new DomainException(DomainConstants.MSG_ONLY_PENDING_ORDERS_CAN_BE_IN_PREPARATION);
        }

       if (requestStatus == OrderStatus.READY && orderStatus != OrderStatus.IN_PREPARATION) {
           throw new DomainException(DomainConstants.MSG_ONLY_IN_PREPARATION_ORDERS_CAN_BE_READY);
       }

        if (requestStatus == OrderStatus.DELIVERED ) {
            throw new DomainException(DomainConstants.MSG_WRONG_METHOD_FOR_DELIVERING_ORDER);
        }

        if(requestStatus == OrderStatus.CANCELLED && orderStatus != OrderStatus.PENDING) {
            throw new DomainException(DomainConstants.MSG_ONLY_PENDING_ORDERS_CAN_BE_CANCELLED);
        }

        if (requestStatus == OrderStatus.PENDING){
            throw new DomainException(DomainConstants.MSG_ORDER_STATUS_CANNOT_BE_PENDING);
        }
    }
    private void validateEmployeeFromSameRestaurant(Long employeeId, Long restaurantId) {
        Employee employee = userServicePort.getUserById(employeeId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.MSG_EMPLOYEE_NOT_FOUND));
        if (employee.getRole() != Role.EMPLOYEE) {
            throw new DomainException(DomainConstants.MSG_USER_IS_NOT_EMPLOYEE);
        }
        if (employee.getRestaurantId() == null || !employee.getRestaurantId().equals(restaurantId)) {
            throw new DomainException(DomainConstants.MSG_EMPLOYEE_NOT_FROM_SAME_RESTAURANT);
        }
    }

    private void validateEmployeeIdIsEmpty(Order order) {
        if (order.getEmployeeId() != null) {
            throw new DomainException(DomainConstants.MSG_ORDER_ALREADY_ASSIGNED);
        }
    }

    private void validateOrderIsAssignable(Order order) {
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new DomainException(DomainConstants.MSG_ONLY_PENDING_ORDERS_CAN_BE_ASSIGNED);
        }
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
