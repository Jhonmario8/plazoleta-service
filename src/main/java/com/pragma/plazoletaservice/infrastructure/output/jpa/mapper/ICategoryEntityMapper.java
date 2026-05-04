package com.pragma.plazoletaservice.infrastructure.output.jpa.mapper;

import com.pragma.plazoletaservice.domain.model.Category;
import com.pragma.plazoletaservice.infrastructure.output.jpa.entites.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ICategoryEntityMapper {

    CategoryEntity toEntity(Category category);
    Category toDomain(CategoryEntity categoryEntity);


}
