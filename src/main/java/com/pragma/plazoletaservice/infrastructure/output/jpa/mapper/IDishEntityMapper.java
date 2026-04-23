package com.pragma.plazoletaservice.infrastructure.output.jpa.mapper;

import com.pragma.plazoletaservice.domain.model.Dish;
import com.pragma.plazoletaservice.domain.model.Restaurant;
import com.pragma.plazoletaservice.infrastructure.output.jpa.entites.DishEntity;
import com.pragma.plazoletaservice.infrastructure.output.jpa.entites.RestaurantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IDishEntityMapper {

    @Mapping(source = "restaurant", target = "restaurantEntity")
    DishEntity toEntity(Dish dish);

    @Mapping(source = "restaurantEntity", target = "restaurant")
    Dish toDomain(DishEntity dishEntity);

    default RestaurantEntity map(Restaurant restaurant) {
        if (restaurant == null) return null;

        RestaurantEntity entity = new RestaurantEntity();
        entity.setId(restaurant.getId());
        return entity;
    }
}
