package com.pragma.plazoletaservice.infrastructure.output.jpa.mapper;

import com.pragma.plazoletaservice.domain.model.Order;
import com.pragma.plazoletaservice.domain.model.OrderDish;
import com.pragma.plazoletaservice.infrastructure.output.jpa.entites.OrderDishEntity;
import com.pragma.plazoletaservice.infrastructure.output.jpa.entites.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IOrderEntityMapper {

    @Mapping(target = "orderDishes", source = "dishes")
    OrderEntity toEntity(Order order);

    @Mapping(target = "dishes", source = "orderDishes")
    Order toDomain(OrderEntity orderEntity);

    OrderDishEntity toOrderDishEntity(OrderDish orderDish);

    OrderDish toOrderDishDomain(OrderDishEntity orderDishEntity);


}
