package com.pragma.plazoletaservice.application.mapper;

import com.pragma.plazoletaservice.application.dto.OrderDto;
import com.pragma.plazoletaservice.domain.model.Order;
import org.mapstruct.Mapper;

import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IOrderMapper {

    Order toDomain(OrderDto dto);

    OrderDto toDto(Order order);
}
