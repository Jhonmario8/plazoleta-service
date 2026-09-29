package com.pragma.plazoletaservice.application.usecase;

import com.pragma.plazoletaservice.application.dto.DishResponseDto;
import com.pragma.plazoletaservice.application.dto.PaginatedResponseDto;
import com.pragma.plazoletaservice.application.mapper.IDishMapper;
import com.pragma.plazoletaservice.domain.api.IAuthenticationPort;
import com.pragma.plazoletaservice.domain.constants.DomainConstants;
import com.pragma.plazoletaservice.domain.exception.ConflictException;
import com.pragma.plazoletaservice.domain.exception.DomainException;
import com.pragma.plazoletaservice.domain.exception.NotFoundException;
import com.pragma.plazoletaservice.domain.exception.UnauthorizedException;
import com.pragma.plazoletaservice.domain.model.Category;
import com.pragma.plazoletaservice.domain.model.Dish;
import com.pragma.plazoletaservice.domain.model.Restaurant;
import com.pragma.plazoletaservice.domain.spi.ICategoryPersistencePort;
import com.pragma.plazoletaservice.domain.spi.IDishPersistencePort;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DishUseCaseTest {

    private static final Long RESTAURANT_ID = 100L;
    private static final Long OWNER_ID = 7L;
    private static final Long CATEGORY_ID = 3L;
    private static final Long DISH_ID = 50L;

    @Mock
    private IDishPersistencePort dishPersistencePort;
    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;
    @Mock
    private IAuthenticationPort authenticationPort;
    @Mock
    private ICategoryPersistencePort categoryPersistencePort;
    @Mock
    private IDishMapper mapper;

    @InjectMocks
    private DishUseCase dishUseCase;

    private static Restaurant restaurant(Long id) {
        return new Restaurant(id, "La Parrilla", "900123", "Calle 1", "+573001112233", "http://logo.png", OWNER_ID);
    }

    private static Category category() {
        return new Category(CATEGORY_ID, "Carnes");
    }

    private static Dish dish(Long id, Restaurant restaurant) {
        return new Dish(id, "Bandeja paisa", 25000, "Plato típico", "http://img.png", category(), restaurant);
    }

    @Nested
    @DisplayName("createDish")
    class CreateDish {

        @Test
        @DisplayName("crea el plato cuando el usuario autenticado es dueño del restaurante")
        void createsDishForOwner() {
            // given
            Restaurant restaurant = restaurant(RESTAURANT_ID);
            Dish dish = dish(null, null);
            Category category = category();
            when(restaurantPersistencePort.getRestaurantById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
            when(dishPersistencePort.existsDishByNameAndRestaurantId(dish.getName(), RESTAURANT_ID)).thenReturn(false);
            when(authenticationPort.getCurrentUserId()).thenReturn(OWNER_ID);
            when(categoryPersistencePort.findCategoryById(CATEGORY_ID)).thenReturn(Optional.of(category));

            // when
            dishUseCase.createDish(dish, RESTAURANT_ID, CATEGORY_ID);

            // then
            assertThat(dish.getRestaurant()).isSameAs(restaurant);
            assertThat(dish.getCategory()).isSameAs(category);
            assertThat(dish.getActive()).isTrue();
            verify(dishPersistencePort).saveDish(dish);
        }

        @Test
        @DisplayName("lanza NotFoundException si el restaurante no existe")
        void throwsWhenRestaurantNotFound() {
            // given
            when(restaurantPersistencePort.getRestaurantById(RESTAURANT_ID)).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> dishUseCase.createDish(dish(null, null), RESTAURANT_ID, CATEGORY_ID))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(DomainConstants.MSG_RESTAURANT_NOT_FOUND);
            verify(dishPersistencePort, never()).saveDish(any());
        }

        @Test
        @DisplayName("lanza ConflictException si ya existe un plato con el mismo nombre en el restaurante")
        void throwsWhenDishNameAlreadyExists() {
            // given
            Dish dish = dish(null, null);
            when(restaurantPersistencePort.getRestaurantById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant(RESTAURANT_ID)));
            when(dishPersistencePort.existsDishByNameAndRestaurantId(dish.getName(), RESTAURANT_ID)).thenReturn(true);

            // when / then
            assertThatThrownBy(() -> dishUseCase.createDish(dish, RESTAURANT_ID, CATEGORY_ID))
                    .isInstanceOf(ConflictException.class)
                    .hasMessage(DomainConstants.MSG_DISH_ALREADY_EXISTS);
            verify(dishPersistencePort, never()).saveDish(any());
        }

        @Test
        @DisplayName("lanza DomainException si la categoría es null")
        void throwsWhenCategoryIdIsNull() {
            // given
            Dish dish = dish(null, null);
            when(restaurantPersistencePort.getRestaurantById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant(RESTAURANT_ID)));
            when(dishPersistencePort.existsDishByNameAndRestaurantId(dish.getName(), RESTAURANT_ID)).thenReturn(false);

            // when / then
            assertThatThrownBy(() -> dishUseCase.createDish(dish, RESTAURANT_ID, null))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(DomainConstants.MSG_CATEGORY_ID_CANNOT_BE_NULL);
            verify(dishPersistencePort, never()).saveDish(any());
        }

        @Test
        @DisplayName("lanza UnauthorizedException si el usuario no es dueño del restaurante")
        void throwsWhenUserIsNotOwner() {
            // given
            Dish dish = dish(null, null);
            when(restaurantPersistencePort.getRestaurantById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant(RESTAURANT_ID)));
            when(dishPersistencePort.existsDishByNameAndRestaurantId(dish.getName(), RESTAURANT_ID)).thenReturn(false);
            when(authenticationPort.getCurrentUserId()).thenReturn(999L);

            // when / then
            assertThatThrownBy(() -> dishUseCase.createDish(dish, RESTAURANT_ID, CATEGORY_ID))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage(DomainConstants.MSG_NOT_RESTAURANT_OWNER);
            verify(categoryPersistencePort, never()).findCategoryById(any());
            verify(dishPersistencePort, never()).saveDish(any());
        }

        @Test
        @DisplayName("lanza NotFoundException si la categoría no existe")
        void throwsWhenCategoryNotFound() {
            // given
            Dish dish = dish(null, null);
            when(restaurantPersistencePort.getRestaurantById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant(RESTAURANT_ID)));
            when(dishPersistencePort.existsDishByNameAndRestaurantId(dish.getName(), RESTAURANT_ID)).thenReturn(false);
            when(authenticationPort.getCurrentUserId()).thenReturn(OWNER_ID);
            when(categoryPersistencePort.findCategoryById(CATEGORY_ID)).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> dishUseCase.createDish(dish, RESTAURANT_ID, CATEGORY_ID))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(DomainConstants.MSG_CATEGORY_N0T_FOUND);
            verify(dishPersistencePort, never()).saveDish(any());
        }
    }

    @Nested
    @DisplayName("updateDish")
    class UpdateDish {

        @Test
        @DisplayName("actualiza nombre, descripción, precio, categoría y estado del plato existente")
        void updatesExistingDish() {
            // given
            Restaurant restaurant = restaurant(RESTAURANT_ID);
            Dish existing = dish(DISH_ID, restaurant);
            Dish changes = new Dish(DISH_ID, "Bandeja especial", 32000, "Con chicharrón extra", "http://img.png",
                    new Category(4L, "Especiales"), null);
            changes.setActive(false);
            when(restaurantPersistencePort.getRestaurantById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
            when(dishPersistencePort.getDishById(DISH_ID)).thenReturn(Optional.of(existing));
            when(authenticationPort.getCurrentUserId()).thenReturn(OWNER_ID);

            // when
            dishUseCase.updateDish(changes, RESTAURANT_ID);

            // then
            assertThat(existing.getName()).isEqualTo("Bandeja especial");
            assertThat(existing.getDescription()).isEqualTo("Con chicharrón extra");
            assertThat(existing.getPrice()).isEqualTo(32000);
            assertThat(existing.getCategory().getName()).isEqualTo("Especiales");
            assertThat(existing.getActive()).isFalse();
            verify(dishPersistencePort).saveDish(existing);
        }

        @Test
        @DisplayName("lanza UnauthorizedException si el usuario no es dueño del restaurante")
        void throwsWhenUserIsNotOwner() {
            // given
            Restaurant restaurant = restaurant(RESTAURANT_ID);
            when(restaurantPersistencePort.getRestaurantById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant));
            when(dishPersistencePort.getDishById(DISH_ID)).thenReturn(Optional.of(dish(DISH_ID, restaurant)));
            when(authenticationPort.getCurrentUserId()).thenReturn(999L);

            // when / then
            assertThatThrownBy(() -> dishUseCase.updateDish(dish(DISH_ID, null), RESTAURANT_ID))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage(DomainConstants.MSG_NOT_RESTAURANT_OWNER);
            verify(dishPersistencePort, never()).saveDish(any());
        }

        @Test
        @DisplayName("lanza ConflictException si el plato pertenece a otro restaurante")
        void throwsWhenDishBelongsToAnotherRestaurant() {
            // given
            when(restaurantPersistencePort.getRestaurantById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant(RESTAURANT_ID)));
            when(dishPersistencePort.getDishById(DISH_ID)).thenReturn(Optional.of(dish(DISH_ID, restaurant(200L))));
            when(authenticationPort.getCurrentUserId()).thenReturn(OWNER_ID);

            // when / then
            assertThatThrownBy(() -> dishUseCase.updateDish(dish(DISH_ID, null), RESTAURANT_ID))
                    .isInstanceOf(ConflictException.class)
                    .hasMessage(DomainConstants.MSG_DISH_RESTAURANT_MISMATCH);
            verify(dishPersistencePort, never()).saveDish(any());
        }

        @Test
        @DisplayName("lanza NotFoundException si el plato no existe")
        void throwsWhenDishNotFound() {
            // given
            when(restaurantPersistencePort.getRestaurantById(RESTAURANT_ID)).thenReturn(Optional.of(restaurant(RESTAURANT_ID)));
            when(dishPersistencePort.getDishById(DISH_ID)).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> dishUseCase.updateDish(dish(DISH_ID, null), RESTAURANT_ID))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(DomainConstants.MSG_DISH_NOT_FOUND);
            verify(dishPersistencePort, never()).saveDish(any());
        }
    }

    @Nested
    @DisplayName("getDishes")
    class GetDishes {

        @Test
        @DisplayName("devuelve la página de platos mapeada con los metadatos de paginación")
        void returnsPaginatedDishes() {
            // given
            Dish dish = dish(DISH_ID, restaurant(RESTAURANT_ID));
            DishResponseDto dto = new DishResponseDto(DISH_ID, "Bandeja paisa", "Plato típico", 25000, "http://img.png", "Carnes");
            when(dishPersistencePort.getDishes(RESTAURANT_ID, CATEGORY_ID, 1, 5))
                    .thenReturn(new PageImpl<>(List.of(dish), PageRequest.of(1, 5), 6));
            when(mapper.toResponseDto(dish)).thenReturn(dto);

            // when
            PaginatedResponseDto<DishResponseDto> result = dishUseCase.getDishes(RESTAURANT_ID, CATEGORY_ID, 1, 5);

            // then
            assertThat(result.getContent()).containsExactly(dto);
            assertThat(result.getPage()).isEqualTo(1);
            assertThat(result.getSize()).isEqualTo(5);
            assertThat(result.getTotalElements()).isEqualTo(6);
            assertThat(result.getTotalPages()).isEqualTo(2);
        }

        @Test
        @DisplayName("devuelve una página vacía cuando no hay platos")
        void returnsEmptyPage() {
            // given
            when(dishPersistencePort.getDishes(RESTAURANT_ID, null, 0, 10))
                    .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));

            // when
            PaginatedResponseDto<DishResponseDto> result = dishUseCase.getDishes(RESTAURANT_ID, null, 0, 10);

            // then
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
            verify(mapper, never()).toResponseDto(any());
        }
    }
}
