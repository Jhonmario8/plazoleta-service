package com.pragma.plazoletaservice.infrastructure.output.jpa.mapper;

import com.pragma.plazoletaservice.domain.model.Restaurant;
import com.pragma.plazoletaservice.infrastructure.output.jpa.entites.RestaurantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IRestaurantEntityMapper {

    Restaurant toDomain(RestaurantEntity restaurantEntity);

    RestaurantEntity toEntity(Restaurant restaurant);

}
