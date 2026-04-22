package com.pragma.plazoletaservice.application.mapper;

import com.pragma.plazoletaservice.application.dto.DishDTO;
import com.pragma.plazoletaservice.domain.model.Dish;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IDishMapper {

    Dish toDomain(DishDTO dishDTO);

}
