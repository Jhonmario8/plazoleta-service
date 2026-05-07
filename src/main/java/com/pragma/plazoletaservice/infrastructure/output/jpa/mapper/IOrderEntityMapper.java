package com.pragma.plazoletaservice.infrastructure.output.jpa.mapper;

import com.pragma.plazoletaservice.domain.model.Order;
import com.pragma.plazoletaservice.domain.model.OrderDish;
import com.pragma.plazoletaservice.infrastructure.output.jpa.entites.DishEntity;
import com.pragma.plazoletaservice.infrastructure.output.jpa.entites.OrderDishEntity;
import com.pragma.plazoletaservice.infrastructure.output.jpa.entites.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IOrderEntityMapper {

    @Mapping(target = "restaurant.id", source = "restaurantId")
    @Mapping(target = "orderDishes", source = "dishes")
    OrderEntity toEntity(Order order);

    @Mapping(target = "restaurantId", source = "restaurant.id")
    @Mapping(target = "dishes", source = "orderDishes")
    Order toDomain(OrderEntity orderEntity);

    @Mapping(target = "dish", source = "dishId")
    OrderDishEntity toOrderDishEntity(OrderDish orderDish);


    @Mapping(target = "dishId", source = "dish.id")
    OrderDish toOrderDishDomain(OrderDishEntity orderDishEntity);

    default DishEntity map(Long value) {
        if (value == null) {
            return null;
        }
        DishEntity dish = new DishEntity();
        dish.setId(value);
        return dish;
    }
}
