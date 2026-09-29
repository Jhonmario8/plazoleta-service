package com.pragma.plazoletaservice.application.usecase;

import com.pragma.plazoletaservice.application.dto.OrderDto;
import com.pragma.plazoletaservice.application.dto.PaginatedResponseDto;
import com.pragma.plazoletaservice.application.mapper.IOrderMapper;
import com.pragma.plazoletaservice.domain.api.IAuthenticationPort;
import com.pragma.plazoletaservice.domain.api.ISmsServicePort;
import com.pragma.plazoletaservice.domain.api.ITraceabilityServicePort;
import com.pragma.plazoletaservice.domain.api.IUserServicePort;
import com.pragma.plazoletaservice.domain.constants.DomainConstants;
import com.pragma.plazoletaservice.domain.exception.DomainException;
import com.pragma.plazoletaservice.domain.exception.NotFoundException;
import com.pragma.plazoletaservice.domain.exception.UnauthorizedException;
import com.pragma.plazoletaservice.domain.model.Dish;
import com.pragma.plazoletaservice.domain.model.Employee;
import com.pragma.plazoletaservice.domain.model.Order;
import com.pragma.plazoletaservice.domain.model.OrderDish;
import com.pragma.plazoletaservice.domain.model.OrderStatus;
import com.pragma.plazoletaservice.domain.model.OrderTraceabilityRequest;
import com.pragma.plazoletaservice.domain.model.Role;
import com.pragma.plazoletaservice.domain.model.Sms;
import com.pragma.plazoletaservice.domain.spi.IDishPersistencePort;
import com.pragma.plazoletaservice.domain.spi.IOrderPersistencePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderUseCaseTest {

    private static final Long ORDER_ID = 1L;
    private static final Long CLIENT_ID = 10L;
    private static final Long EMPLOYEE_ID = 20L;
    private static final Long RESTAURANT_ID = 100L;
    private static final String CLIENT_PHONE = "3001234567";
    private static final String CLIENT_WHATSAPP = "whatsapp:+57" + CLIENT_PHONE;

    @Mock
    private IOrderPersistencePort orderPersistencePort;
    @Mock
    private IAuthenticationPort authenticationPort;
    @Mock
    private IDishPersistencePort dishPersistencePort;
    @Mock
    private IUserServicePort userServicePort;
    @Mock
    private IOrderMapper mapper;
    @Mock
    private ISmsServicePort smsServicePort;
    @Mock
    private ITraceabilityServicePort traceabilityServicePort;

    @InjectMocks
    private OrderUseCase orderUseCase;

    private static Order orderWithStatus(OrderStatus status) {
        Order order = new Order();
        order.setId(ORDER_ID);
        order.setClientId(CLIENT_ID);
        order.setRestaurantId(RESTAURANT_ID);
        order.setStatus(status);
        order.setDishes(List.of(new OrderDish(null, null, 5L, 2)));
        return order;
    }

    private static Employee client() {
        Employee client = new Employee();
        client.setId(CLIENT_ID);
        client.setPhoneNumber(CLIENT_PHONE);
        client.setRole(Role.CLIENT);
        return client;
    }

    private static Employee employee(Role role, Long restaurantId) {
        Employee employee = new Employee();
        employee.setId(EMPLOYEE_ID);
        employee.setRole(role);
        employee.setRestaurantId(restaurantId);
        return employee;
    }

    private static OrderTraceabilityRequest traceabilityWithState(OrderStatus state) {
        OrderTraceabilityRequest request = new OrderTraceabilityRequest();
        request.setOrderId(ORDER_ID);
        request.setNewState(state.name());
        return request;
    }

    @Nested
    @DisplayName("createOrder")
    class CreateOrder {

        @Test
        @DisplayName("guarda el pedido en PENDING asignado al cliente autenticado")
        void savesPendingOrderForCurrentClient() {
            // given
            Order order = orderWithStatus(null);
            order.setClientId(null);
            when(authenticationPort.getCurrentUserId()).thenReturn(CLIENT_ID);
            when(orderPersistencePort.existsActiveOrderByClientId(CLIENT_ID)).thenReturn(false);
            when(dishPersistencePort.getDishesByIds(List.of(5L), RESTAURANT_ID)).thenReturn(List.of(mock(Dish.class)));

            // when
            orderUseCase.createOrder(order);

            // then
            assertThat(order.getClientId()).isEqualTo(CLIENT_ID);
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
            assertThat(order.getDate()).isNotNull();
            verify(orderPersistencePort).saveOrder(order);
            ArgumentCaptor<OrderTraceabilityRequest> captor = ArgumentCaptor.forClass(OrderTraceabilityRequest.class);
            verify(traceabilityServicePort).saveTraceabilityRecord(captor.capture());
            assertThat(captor.getValue().getNewState()).isEqualTo(OrderStatus.PENDING.name());
            assertThat(captor.getValue().getPreviousState()).isNull();
            assertThat(captor.getValue().getClientId()).isEqualTo(CLIENT_ID);
        }

        @Test
        @DisplayName("rechaza el pedido si el cliente ya tiene un pedido activo")
        void rejectsWhenClientHasActiveOrder() {
            // given
            Order order = orderWithStatus(null);
            when(authenticationPort.getCurrentUserId()).thenReturn(CLIENT_ID);
            when(orderPersistencePort.existsActiveOrderByClientId(CLIENT_ID)).thenReturn(true);

            // when / then
            assertThatThrownBy(() -> orderUseCase.createOrder(order))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(DomainConstants.MSG_CLIENT_HAS_ACTIVE_ORDER);
            verify(orderPersistencePort, never()).saveOrder(any());
            verify(traceabilityServicePort, never()).saveTraceabilityRecord(any());
        }

        @Test
        @DisplayName("rechaza el pedido si algún plato no pertenece al restaurante")
        void rejectsWhenDishesDoNotBelongToRestaurant() {
            // given
            Order order = orderWithStatus(null);
            when(authenticationPort.getCurrentUserId()).thenReturn(CLIENT_ID);
            when(orderPersistencePort.existsActiveOrderByClientId(CLIENT_ID)).thenReturn(false);
            when(dishPersistencePort.getDishesByIds(anyList(), any())).thenReturn(List.of());

            // when / then
            assertThatThrownBy(() -> orderUseCase.createOrder(order))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(DomainConstants.MSG_SOME_DISHES_NOT_FOUND_IN_RESTAURANT);
            verify(orderPersistencePort, never()).saveOrder(any());
        }
    }

    @Nested
    @DisplayName("getOrders")
    class GetOrders {

        @Test
        @DisplayName("devuelve la página de pedidos mapeada cuando el usuario es empleado")
        void returnsPaginatedOrdersForEmployee() {
            // given
            Order order = orderWithStatus(OrderStatus.PENDING);
            OrderDto dto = new OrderDto();
            when(authenticationPort.getCurrentUserId()).thenReturn(EMPLOYEE_ID);
            when(userServicePort.getUserRole(EMPLOYEE_ID)).thenReturn(Role.EMPLOYEE);
            when(orderPersistencePort.getOrders(RESTAURANT_ID, OrderStatus.PENDING, 0, 10))
                    .thenReturn(new PageImpl<>(List.of(order), PageRequest.of(0, 10), 1));
            when(mapper.toDto(order)).thenReturn(dto);

            // when
            PaginatedResponseDto<OrderDto> result = orderUseCase.getOrders(RESTAURANT_ID, OrderStatus.PENDING, 0, 10);

            // then
            assertThat(result.getContent()).containsExactly(dto);
            assertThat(result.getPage()).isZero();
            assertThat(result.getSize()).isEqualTo(10);
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getTotalPages()).isEqualTo(1);
        }

        @Test
        @DisplayName("rechaza la consulta si el usuario no es empleado")
        void rejectsNonEmployee() {
            // given
            when(authenticationPort.getCurrentUserId()).thenReturn(CLIENT_ID);
            when(userServicePort.getUserRole(CLIENT_ID)).thenReturn(Role.CLIENT);

            // when / then
            assertThatThrownBy(() -> orderUseCase.getOrders(RESTAURANT_ID, null, 0, 10))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage(DomainConstants.MSG_ONLY_EMPLOYEE_CAN_GET_ORDERS);
            verify(orderPersistencePort, never()).getOrders(any(), any(), anyInt(), anyInt());
        }
    }

    @Nested
    @DisplayName("assignEmployeeToOrder")
    class AssignEmployeeToOrder {

        @Test
        @DisplayName("asigna el empleado y pasa el pedido de PENDING a IN_PREPARATION")
        void assignsEmployeeToPendingOrder() {
            // given
            Order order = orderWithStatus(OrderStatus.PENDING);
            when(orderPersistencePort.getOrderById(ORDER_ID)).thenReturn(Optional.of(order));
            when(userServicePort.getUserById(EMPLOYEE_ID)).thenReturn(Optional.of(employee(Role.EMPLOYEE, RESTAURANT_ID)));

            // when
            orderUseCase.assignEmployeeToOrder(ORDER_ID, EMPLOYEE_ID);

            // then
            assertThat(order.getEmployeeId()).isEqualTo(EMPLOYEE_ID);
            assertThat(order.getStatus()).isEqualTo(OrderStatus.IN_PREPARATION);
            verify(orderPersistencePort).saveOrder(order);
            ArgumentCaptor<OrderTraceabilityRequest> captor = ArgumentCaptor.forClass(OrderTraceabilityRequest.class);
            verify(traceabilityServicePort).saveTraceabilityRecord(captor.capture());
            OrderTraceabilityRequest record = captor.getValue();
            assertThat(record.getOrderId()).isEqualTo(ORDER_ID);
            assertThat(record.getEmployeeId()).isEqualTo(EMPLOYEE_ID);
            assertThat(record.getPreviousState()).isEqualTo(OrderStatus.PENDING.name());
            assertThat(record.getNewState()).isEqualTo(OrderStatus.IN_PREPARATION.name());
        }

        @Test
        @DisplayName("lanza NotFoundException si el pedido no existe")
        void throwsWhenOrderNotFound() {
            // given
            when(orderPersistencePort.getOrderById(ORDER_ID)).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> orderUseCase.assignEmployeeToOrder(ORDER_ID, EMPLOYEE_ID))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(DomainConstants.MSG_ORDER_NOT_FOUND);
            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @ParameterizedTest(name = "estado {0}")
        @CsvSource({"IN_PREPARATION", "READY", "DELIVERED", "CANCELLED"})
        @DisplayName("rechaza la asignación si el pedido no está en PENDING")
        void rejectsWhenOrderIsNotPending(OrderStatus status) {
            // given
            when(orderPersistencePort.getOrderById(ORDER_ID)).thenReturn(Optional.of(orderWithStatus(status)));

            // when / then
            assertThatThrownBy(() -> orderUseCase.assignEmployeeToOrder(ORDER_ID, EMPLOYEE_ID))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(DomainConstants.MSG_ONLY_PENDING_ORDERS_CAN_BE_ASSIGNED);
            verify(userServicePort, never()).getUserById(anyLong());
            verify(orderPersistencePort, never()).saveOrder(any());
            verify(traceabilityServicePort, never()).saveTraceabilityRecord(any());
        }

        @Test
        @DisplayName("rechaza la asignación si el pedido ya tiene empleado")
        void rejectsWhenOrderAlreadyAssigned() {
            // given
            Order order = orderWithStatus(OrderStatus.PENDING);
            order.setEmployeeId(99L);
            when(orderPersistencePort.getOrderById(ORDER_ID)).thenReturn(Optional.of(order));

            // when / then
            assertThatThrownBy(() -> orderUseCase.assignEmployeeToOrder(ORDER_ID, EMPLOYEE_ID))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(DomainConstants.MSG_ORDER_ALREADY_ASSIGNED);
            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("lanza NotFoundException si el empleado no existe")
        void throwsWhenEmployeeNotFound() {
            // given
            when(orderPersistencePort.getOrderById(ORDER_ID)).thenReturn(Optional.of(orderWithStatus(OrderStatus.PENDING)));
            when(userServicePort.getUserById(EMPLOYEE_ID)).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> orderUseCase.assignEmployeeToOrder(ORDER_ID, EMPLOYEE_ID))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(DomainConstants.MSG_EMPLOYEE_NOT_FOUND);
            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("rechaza la asignación si el usuario no tiene rol EMPLOYEE")
        void rejectsWhenUserIsNotEmployee() {
            // given
            when(orderPersistencePort.getOrderById(ORDER_ID)).thenReturn(Optional.of(orderWithStatus(OrderStatus.PENDING)));
            when(userServicePort.getUserById(EMPLOYEE_ID)).thenReturn(Optional.of(employee(Role.OWNER, RESTAURANT_ID)));

            // when / then
            assertThatThrownBy(() -> orderUseCase.assignEmployeeToOrder(ORDER_ID, EMPLOYEE_ID))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(DomainConstants.MSG_USER_IS_NOT_EMPLOYEE);
            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("rechaza la asignación si el empleado es de otro restaurante")
        void rejectsWhenEmployeeFromAnotherRestaurant() {
            // given
            when(orderPersistencePort.getOrderById(ORDER_ID)).thenReturn(Optional.of(orderWithStatus(OrderStatus.PENDING)));
            when(userServicePort.getUserById(EMPLOYEE_ID)).thenReturn(Optional.of(employee(Role.EMPLOYEE, 999L)));

            // when / then
            assertThatThrownBy(() -> orderUseCase.assignEmployeeToOrder(ORDER_ID, EMPLOYEE_ID))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(DomainConstants.MSG_EMPLOYEE_NOT_FROM_SAME_RESTAURANT);
            verify(orderPersistencePort, never()).saveOrder(any());
            verify(traceabilityServicePort, never()).saveTraceabilityRecord(any());
        }

        @Test
        @DisplayName("rechaza la asignación si el empleado no tiene restaurante")
        void rejectsWhenEmployeeHasNoRestaurant() {
            // given
            when(orderPersistencePort.getOrderById(ORDER_ID)).thenReturn(Optional.of(orderWithStatus(OrderStatus.PENDING)));
            when(userServicePort.getUserById(EMPLOYEE_ID)).thenReturn(Optional.of(employee(Role.EMPLOYEE, null)));

            // when / then
            assertThatThrownBy(() -> orderUseCase.assignEmployeeToOrder(ORDER_ID, EMPLOYEE_ID))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(DomainConstants.MSG_EMPLOYEE_NOT_FROM_SAME_RESTAURANT);
            verify(orderPersistencePort, never()).saveOrder(any());
        }
    }

    @Nested
    @DisplayName("updateOrderStatus")
    class UpdateOrderStatus {

        @Test
        @DisplayName("PENDING -> IN_PREPARATION actualiza estado y trazabilidad sin enviar SMS")
        void pendingToInPreparation() {
            // given
            Order order = orderWithStatus(OrderStatus.PENDING);
            OrderTraceabilityRequest traceability = traceabilityWithState(OrderStatus.PENDING);
            when(orderPersistencePort.getOrderById(ORDER_ID)).thenReturn(Optional.of(order));
            when(userServicePort.getUserById(CLIENT_ID)).thenReturn(Optional.of(client()));
            when(traceabilityServicePort.findTraceabilityById(ORDER_ID)).thenReturn(traceability);

            // when
            orderUseCase.updateOrderStatus(ORDER_ID, OrderStatus.IN_PREPARATION);

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.IN_PREPARATION);
            assertThat(traceability.getPreviousState()).isEqualTo(OrderStatus.PENDING.name());
            assertThat(traceability.getNewState()).isEqualTo(OrderStatus.IN_PREPARATION.name());
            verify(traceabilityServicePort).saveTraceabilityRecord(traceability);
            verify(orderPersistencePort).saveOrder(order);
            verify(smsServicePort, never()).sendSms(any());
        }

        @Test
        @DisplayName("IN_PREPARATION -> READY envía SMS al cliente con el código de entrega")
        void inPreparationToReadySendsSmsWithOrderCode() {
            // given
            Order order = orderWithStatus(OrderStatus.IN_PREPARATION);
            when(orderPersistencePort.getOrderById(ORDER_ID)).thenReturn(Optional.of(order));
            when(userServicePort.getUserById(CLIENT_ID)).thenReturn(Optional.of(client()));
            when(traceabilityServicePort.findTraceabilityById(ORDER_ID))
                    .thenReturn(traceabilityWithState(OrderStatus.IN_PREPARATION));

            // when
            orderUseCase.updateOrderStatus(ORDER_ID, OrderStatus.READY);

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.READY);
            assertThat(order.getOrderCode()).isBetween(1000, 9999);

            ArgumentCaptor<Sms> smsCaptor = ArgumentCaptor.forClass(Sms.class);
            verify(smsServicePort).sendSms(smsCaptor.capture());
            Sms sms = smsCaptor.getValue();
            assertThat(sms.getDestinationPhoneNumber()).isEqualTo(CLIENT_WHATSAPP);
            assertThat(sms.getMessage()).isEqualTo(DomainConstants.MSG_SMS_ORDER_READY + order.getOrderCode());
            verify(orderPersistencePort).saveOrder(order);
        }

        @ParameterizedTest(name = "{0} -> {1}")
        @CsvSource({
                "PENDING, READY",
                "READY, IN_PREPARATION",
                "IN_PREPARATION, IN_PREPARATION",
                "IN_PREPARATION, DELIVERED",
                "READY, DELIVERED",
                "IN_PREPARATION, PENDING",
                "IN_PREPARATION, CANCELLED",
                "READY, CANCELLED"
        })
        @DisplayName("rechaza transiciones inválidas sin guardar ni enviar SMS")
        void rejectsInvalidTransitions(OrderStatus current, OrderStatus requested) {
            // given
            when(orderPersistencePort.getOrderById(ORDER_ID)).thenReturn(Optional.of(orderWithStatus(current)));
            when(userServicePort.getUserById(CLIENT_ID)).thenReturn(Optional.of(client()));

            // when / then
            assertThatThrownBy(() -> orderUseCase.updateOrderStatus(ORDER_ID, requested))
                    .isInstanceOf(DomainException.class);
            verify(traceabilityServicePort, never()).saveTraceabilityRecord(any());
            verify(smsServicePort, never()).sendSms(any());
            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("no permite marcar DELIVERED por este método (debe usarse deliverOrder)")
        void rejectsDeliveredThroughThisMethod() {
            // given
            when(orderPersistencePort.getOrderById(ORDER_ID)).thenReturn(Optional.of(orderWithStatus(OrderStatus.READY)));
            when(userServicePort.getUserById(CLIENT_ID)).thenReturn(Optional.of(client()));

            // when / then
            assertThatThrownBy(() -> orderUseCase.updateOrderStatus(ORDER_ID, OrderStatus.DELIVERED))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(DomainConstants.MSG_WRONG_METHOD_FOR_DELIVERING_ORDER);
        }

        @Test
        @DisplayName("lanza NotFoundException si el pedido no existe")
        void throwsWhenOrderNotFound() {
            // given
            when(orderPersistencePort.getOrderById(ORDER_ID)).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> orderUseCase.updateOrderStatus(ORDER_ID, OrderStatus.READY))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(DomainConstants.MSG_ORDER_NOT_FOUND);
            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("lanza NotFoundException si el cliente del pedido no existe")
        void throwsWhenClientNotFound() {
            // given
            when(orderPersistencePort.getOrderById(ORDER_ID)).thenReturn(Optional.of(orderWithStatus(OrderStatus.IN_PREPARATION)));
            when(userServicePort.getUserById(CLIENT_ID)).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> orderUseCase.updateOrderStatus(ORDER_ID, OrderStatus.READY))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(DomainConstants.MSG_CLIENT_NOT_FOUND);
            verify(smsServicePort, never()).sendSms(any());
            verify(orderPersistencePort, never()).saveOrder(any());
        }
    }

    @Nested
    @DisplayName("cancelOrder")
    class CancelOrder {

        @Test
        @DisplayName("cancela un pedido PENDING, cierra la trazabilidad y notifica al cliente")
        void cancelsPendingOrder() {
            // given
            Order order = orderWithStatus(OrderStatus.PENDING);
            OrderTraceabilityRequest traceability = traceabilityWithState(OrderStatus.PENDING);
            when(orderPersistencePort.getOrderById(ORDER_ID)).thenReturn(Optional.of(order));
            when(userServicePort.getUserById(CLIENT_ID)).thenReturn(Optional.of(client()));
            when(traceabilityServicePort.findTraceabilityById(ORDER_ID)).thenReturn(traceability);

            // when
            orderUseCase.cancelOrder(ORDER_ID);

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
            assertThat(traceability.getPreviousState()).isEqualTo(OrderStatus.PENDING.name());
            assertThat(traceability.getNewState()).isEqualTo(OrderStatus.CANCELLED.name());
            assertThat(traceability.getEndTime()).isNotNull();
            verify(traceabilityServicePort).saveTraceabilityRecord(traceability);

            ArgumentCaptor<Sms> smsCaptor = ArgumentCaptor.forClass(Sms.class);
            verify(smsServicePort).sendSms(smsCaptor.capture());
            assertThat(smsCaptor.getValue().getDestinationPhoneNumber()).isEqualTo(CLIENT_WHATSAPP);
            assertThat(smsCaptor.getValue().getMessage()).isEqualTo(DomainConstants.MSG_SMS_ORDER_CANCELLED);
            verify(orderPersistencePort).saveOrder(order);
        }

        @ParameterizedTest(name = "estado {0}")
        @CsvSource({"IN_PREPARATION", "READY", "DELIVERED", "CANCELLED"})
        @DisplayName("rechaza cancelar si no está PENDING y avisa al cliente por SMS")
        void rejectsWhenNotPending(OrderStatus status) {
            // given
            Order order = orderWithStatus(status);
            when(orderPersistencePort.getOrderById(ORDER_ID)).thenReturn(Optional.of(order));
            when(userServicePort.getUserById(CLIENT_ID)).thenReturn(Optional.of(client()));

            // when / then
            assertThatThrownBy(() -> orderUseCase.cancelOrder(ORDER_ID))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(DomainConstants.MSG_ONLY_PENDING_ORDERS_CAN_BE_CANCELLED);

            ArgumentCaptor<Sms> smsCaptor = ArgumentCaptor.forClass(Sms.class);
            verify(smsServicePort).sendSms(smsCaptor.capture());
            assertThat(smsCaptor.getValue().getMessage()).isEqualTo(DomainConstants.MSG_ONLY_PENDING_ORDERS_CAN_BE_CANCELLED);
            assertThat(order.getStatus()).isEqualTo(status);
            verify(traceabilityServicePort, never()).saveTraceabilityRecord(any());
            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @Test
        @DisplayName("lanza NotFoundException si el pedido no existe")
        void throwsWhenOrderNotFound() {
            // given
            when(orderPersistencePort.getOrderById(ORDER_ID)).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> orderUseCase.cancelOrder(ORDER_ID))
                    .isInstanceOf(NotFoundException.class);
            verify(smsServicePort, never()).sendSms(any());
        }
    }

    @Nested
    @DisplayName("deliverOrder")
    class DeliverOrder {

        private static final int ORDER_CODE = 4321;

        @Test
        @DisplayName("entrega un pedido READY con el código correcto")
        void deliversReadyOrderWithValidCode() {
            // given
            Order order = orderWithStatus(OrderStatus.READY);
            order.setOrderCode(ORDER_CODE);
            OrderTraceabilityRequest traceability = traceabilityWithState(OrderStatus.READY);
            when(orderPersistencePort.getOrderById(ORDER_ID)).thenReturn(Optional.of(order));
            when(userServicePort.getUserById(CLIENT_ID)).thenReturn(Optional.of(client()));
            when(traceabilityServicePort.findTraceabilityById(ORDER_ID)).thenReturn(traceability);

            // when
            orderUseCase.deliverOrder(ORDER_ID, ORDER_CODE);

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
            assertThat(traceability.getPreviousState()).isEqualTo(OrderStatus.READY.name());
            assertThat(traceability.getNewState()).isEqualTo(OrderStatus.DELIVERED.name());
            assertThat(traceability.getEndTime()).isNotNull();
            verify(traceabilityServicePort).saveTraceabilityRecord(traceability);

            ArgumentCaptor<Sms> smsCaptor = ArgumentCaptor.forClass(Sms.class);
            verify(smsServicePort).sendSms(smsCaptor.capture());
            assertThat(smsCaptor.getValue().getDestinationPhoneNumber()).isEqualTo(CLIENT_WHATSAPP);
            assertThat(smsCaptor.getValue().getMessage()).isEqualTo(DomainConstants.MSG_SMS_ORDER_DELIVERED);
            verify(orderPersistencePort).saveOrder(order);
        }

        @Test
        @DisplayName("rechaza la entrega si el código no coincide")
        void rejectsInvalidCode() {
            // given
            Order order = orderWithStatus(OrderStatus.READY);
            order.setOrderCode(ORDER_CODE);
            when(orderPersistencePort.getOrderById(ORDER_ID)).thenReturn(Optional.of(order));
            when(userServicePort.getUserById(CLIENT_ID)).thenReturn(Optional.of(client()));

            // when / then
            assertThatThrownBy(() -> orderUseCase.deliverOrder(ORDER_ID, 1111))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(DomainConstants.MSG_INVALID_ORDER_CODE);
            assertThat(order.getStatus()).isEqualTo(OrderStatus.READY);
            verify(traceabilityServicePort, never()).saveTraceabilityRecord(any());
            verify(smsServicePort, never()).sendSms(any());
            verify(orderPersistencePort, never()).saveOrder(any());
        }

        @ParameterizedTest(name = "estado {0}")
        @CsvSource({"PENDING", "IN_PREPARATION", "DELIVERED", "CANCELLED"})
        @DisplayName("rechaza la entrega si el pedido no está READY")
        void rejectsWhenNotReady(OrderStatus status) {
            // given
            Order order = orderWithStatus(status);
            order.setOrderCode(ORDER_CODE);
            when(orderPersistencePort.getOrderById(ORDER_ID)).thenReturn(Optional.of(order));
            when(userServicePort.getUserById(CLIENT_ID)).thenReturn(Optional.of(client()));

            // when / then
            assertThatThrownBy(() -> orderUseCase.deliverOrder(ORDER_ID, ORDER_CODE))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(DomainConstants.MSG_ONLY_READY_ORDERS_CAN_BE_DELIVERED);
            verify(smsServicePort, never()).sendSms(any());
            verify(orderPersistencePort, never()).saveOrder(any());
        }
    }
}
