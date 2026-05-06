package com.pragma.plazoletaservice.application.dto;

import com.pragma.plazoletaservice.application.constants.ApplicationConstants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private Long id;

    @NotNull(message = ApplicationConstants.ORDER_RESTAURANT_ID_CANNOT_BE_NULL)
    private Long restaurantId;
    private LocalDateTime date;
    private String status;

    @NotEmpty(message = ApplicationConstants.ORDER_DISHES_CANNOT_BE_EMPTY)
    @Valid
    private List<OrderDishDto> dishes;
}
