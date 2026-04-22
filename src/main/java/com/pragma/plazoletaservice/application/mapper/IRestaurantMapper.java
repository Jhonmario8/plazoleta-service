package com.pragma.plazoletaservice.application.mapper;

import com.pragma.plazoletaservice.application.dto.RestaurantDTO;
import com.pragma.plazoletaservice.domain.model.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IRestaurantMapper {

    Restaurant toDomain(RestaurantDTO restaurantDTO);
}
