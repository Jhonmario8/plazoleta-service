package com.pragma.plazoletaservice.infrastructure.output.jpa.mapper;

import com.pragma.plazoletaservice.domain.model.Order;
import com.pragma.plazoletaservice.infrastructure.output.jpa.entites.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IOrderEntityMapper {

    @Mapping(target = "restaurant.id", source = "restaurantId")
    OrderEntity toEntity(Order order);
}
