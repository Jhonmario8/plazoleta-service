package com.pragma.plazoletaservice.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pragma.plazoletaservice.application.constants.ApplicationConstants;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OrderDishDto {

    private Long id;
    private Long orderId;

    @NotNull(message = ApplicationConstants.ORDER_DISH_ID_CANNOT_BE_NULL)
    private Long dishId;

    @NotNull(message = ApplicationConstants.ORDER_DISH_QUANTITY_CANNOT_BE_NULL)
    private Integer quantity;
}
