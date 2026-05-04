package com.pragma.plazoletaservice.application.dto;

import com.pragma.plazoletaservice.application.constants.ApplicationConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DishDTO {
    private Long id;
    @NotBlank(message = ApplicationConstants.DISH_NAME_CANNOT_BE_BLANK_MESSAGE)
    private String name;
    @NotNull(message = ApplicationConstants.DISH_PRICE_CANNOT_BE_NULL_MESSAGE)
    private Integer price;
    @NotBlank(message = ApplicationConstants.DISH_DESCRIPTION_CANNOT_BE_BLANK_MESSAGE)
    @Size(min = 1, message = ApplicationConstants.DISH_DESCRIPTION_MIN_LENGTH_MESSAGE)
    private String description;
    @NotBlank(message = ApplicationConstants.LOGO_CANNOT_BE_BLANK_MESSAGE)
    private String urlImage;

    private Long categoryId;

    private Long restaurantId;
}
