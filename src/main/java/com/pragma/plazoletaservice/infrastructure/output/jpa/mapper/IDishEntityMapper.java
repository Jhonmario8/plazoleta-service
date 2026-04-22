package com.pragma.plazoletaservice.infrastructure.output.jpa.mapper;

import com.pragma.plazoletaservice.domain.model.Dish;
import com.pragma.plazoletaservice.infrastructure.output.jpa.entites.DishEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IDishEntityMapper {

    DishEntity toEntity(Dish dish);

}
