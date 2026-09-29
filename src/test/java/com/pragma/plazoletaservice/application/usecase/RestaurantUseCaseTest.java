package com.pragma.plazoletaservice.application.usecase;

import com.pragma.plazoletaservice.application.dto.PaginatedResponseDto;
import com.pragma.plazoletaservice.application.dto.RestaurantResponseDto;
import com.pragma.plazoletaservice.application.mapper.IRestaurantMapper;
import com.pragma.plazoletaservice.domain.api.IAuthenticationPort;
import com.pragma.plazoletaservice.domain.api.IUserServicePort;
import com.pragma.plazoletaservice.domain.constants.DomainConstants;
import com.pragma.plazoletaservice.domain.exception.ConflictException;
import com.pragma.plazoletaservice.domain.exception.NotFoundException;
import com.pragma.plazoletaservice.domain.exception.UnauthorizedException;
import com.pragma.plazoletaservice.domain.model.Employee;
import com.pragma.plazoletaservice.domain.model.Restaurant;
import com.pragma.plazoletaservice.domain.model.Role;
import com.pragma.plazoletaservice.domain.spi.IRestaurantPersistencePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantUseCaseTest {

    private static final Long OWNER_ID = 7L;
    private static final Long RESTAURANT_ID = 100L;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;
    @Mock
    private IAuthenticationPort authenticationPort;
    @Mock
    private IUserServicePort userServicePort;
    @Mock
    private IRestaurantMapper restaurantMapper;

    @InjectMocks
    private RestaurantUseCase restaurantUseCase;

    private static Restaurant restaurant() {
        return new Restaurant(RESTAURANT_ID, "La Parrilla", "900123", "Calle 1", "+573001112233", "http://logo.png", OWNER_ID);
    }

    @Nested
    @DisplayName("createRestaurant")
    class CreateRestaurant {

        @Test
        @DisplayName("guarda el restaurante cuando el dueño tiene rol OWNER y no hay duplicados")
        void savesRestaurant() {
            // given
            Restaurant restaurant = restaurant();
            when(userServicePort.getUserRole(OWNER_ID)).thenReturn(Role.OWNER);
            when(restaurantPersistencePort.existsByNit(restaurant.getNit())).thenReturn(false);
            when(restaurantPersistencePort.existsByPhoneNumber(restaurant.getPhoneNumber())).thenReturn(false);

            // when
            restaurantUseCase.createRestaurant(restaurant);

            // then
            verify(restaurantPersistencePort).saveRestaurant(restaurant);
        }

        @Test
        @DisplayName("lanza NotFoundException si el usuario dueño no existe (rol null)")
        void throwsWhenOwnerNotFound() {
            // given
            when(userServicePort.getUserRole(OWNER_ID)).thenReturn(null);

            // when / then
            assertThatThrownBy(() -> restaurantUseCase.createRestaurant(restaurant()))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(DomainConstants.MSG_OWNER_NOT_FOUND);
            verify(restaurantPersistencePort, never()).saveRestaurant(any());
        }

        @Test
        @DisplayName("lanza UnauthorizedException si el usuario asignado no es OWNER")
        void throwsWhenUserIsNotOwner() {
            // given
            when(userServicePort.getUserRole(OWNER_ID)).thenReturn(Role.CLIENT);

            // when / then
            assertThatThrownBy(() -> restaurantUseCase.createRestaurant(restaurant()))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage(DomainConstants.ONLY_OWNER_CAN_BE_ASSIGNED_MESSAGE);
            verify(restaurantPersistencePort, never()).saveRestaurant(any());
        }

        @Test
        @DisplayName("lanza ConflictException si el NIT ya existe")
        void throwsWhenNitExists() {
            // given
            Restaurant restaurant = restaurant();
            when(userServicePort.getUserRole(OWNER_ID)).thenReturn(Role.OWNER);
            when(restaurantPersistencePort.existsByNit(restaurant.getNit())).thenReturn(true);

            // when / then
            assertThatThrownBy(() -> restaurantUseCase.createRestaurant(restaurant))
                    .isInstanceOf(ConflictException.class)
                    .hasMessage(DomainConstants.MSG_NIT_ALREADY_EXISTS);
            verify(restaurantPersistencePort, never()).saveRestaurant(any());
        }

        @Test
        @DisplayName("lanza ConflictException si el teléfono ya existe")
        void throwsWhenPhoneExists() {
            // given
            Restaurant restaurant = restaurant();
            when(userServicePort.getUserRole(OWNER_ID)).thenReturn(Role.OWNER);
            when(restaurantPersistencePort.existsByNit(restaurant.getNit())).thenReturn(false);
            when(restaurantPersistencePort.existsByPhoneNumber(restaurant.getPhoneNumber())).thenReturn(true);

            // when / then
            assertThatThrownBy(() -> restaurantUseCase.createRestaurant(restaurant))
                    .isInstanceOf(ConflictException.class)
                    .hasMessage(DomainConstants.MSG_PHONE_NUMBER_ALREADY_EXISTS);
            verify(restaurantPersistencePort, never()).saveRestaurant(any());
        }
    }

    @Nested
    @DisplayName("createEmployee")
    class CreateEmployee {

        @Test
        @DisplayName("asigna el restaurante del propietario al empleado y lo envía a user-service")
        void createsEmployeeInOwnersRestaurant() {
            // given
            Employee employee = new Employee();
            when(authenticationPort.getCurrentUserId()).thenReturn(OWNER_ID);
            when(authenticationPort.getCurrentUserRole()).thenReturn(Role.OWNER);
            when(restaurantPersistencePort.getRestaurantByOwnerId(OWNER_ID)).thenReturn(Optional.of(restaurant()));

            // when
            restaurantUseCase.createEmployee(employee);

            // then
            assertThat(employee.getRestaurantId()).isEqualTo(RESTAURANT_ID);
            verify(userServicePort).createEmployee(employee);
        }

        @Test
        @DisplayName("lanza UnauthorizedException si quien llama no es OWNER")
        void throwsWhenCallerIsNotOwner() {
            // given
            when(authenticationPort.getCurrentUserId()).thenReturn(OWNER_ID);
            when(authenticationPort.getCurrentUserRole()).thenReturn(Role.EMPLOYEE);

            // when / then
            assertThatThrownBy(() -> restaurantUseCase.createEmployee(new Employee()))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage(DomainConstants.ONLY_OWNER_CAN_CREATE_EMPLOYEES);
            verify(restaurantPersistencePort, never()).getRestaurantByOwnerId(anyLong());
            verify(userServicePort, never()).createEmployee(any());
        }

        @Test
        @DisplayName("lanza NotFoundException si el propietario no tiene restaurante")
        void throwsWhenOwnerHasNoRestaurant() {
            // given
            when(authenticationPort.getCurrentUserId()).thenReturn(OWNER_ID);
            when(authenticationPort.getCurrentUserRole()).thenReturn(Role.OWNER);
            when(restaurantPersistencePort.getRestaurantByOwnerId(OWNER_ID)).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> restaurantUseCase.createEmployee(new Employee()))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(DomainConstants.MSG_RESTAURANT_NOT_FOUND);
            verify(userServicePort, never()).createEmployee(any());
        }
    }

    @Nested
    @DisplayName("getRestaurants")
    class GetRestaurants {

        @Test
        @DisplayName("consulta con PageRequest(page, size) y devuelve la página mapeada")
        void returnsPaginatedRestaurants() {
            // given
            Restaurant restaurant = restaurant();
            RestaurantResponseDto dto = new RestaurantResponseDto();
            dto.setName("La Parrilla");
            when(restaurantPersistencePort.getRestaurants(PageRequest.of(2, 3)))
                    .thenReturn(new PageImpl<>(List.of(restaurant), PageRequest.of(2, 3), 7));
            when(restaurantMapper.toResponse(restaurant)).thenReturn(dto);

            // when
            PaginatedResponseDto<RestaurantResponseDto> result = restaurantUseCase.getRestaurants(2, 3);

            // then
            assertThat(result.getContent()).containsExactly(dto);
            assertThat(result.getPage()).isEqualTo(2);
            assertThat(result.getSize()).isEqualTo(3);
            assertThat(result.getTotalElements()).isEqualTo(7);
            assertThat(result.getTotalPages()).isEqualTo(3);
        }
    }
}
