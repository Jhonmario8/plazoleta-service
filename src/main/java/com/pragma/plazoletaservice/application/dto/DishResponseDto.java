package com.pragma.plazoletaservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DishResponseDto {

    private Long id;
    private String name;
    private String description;
    private Integer price;
    private String urlImage;
    private String categoryName;

}
