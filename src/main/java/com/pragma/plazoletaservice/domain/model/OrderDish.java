package com.pragma.plazoletaservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDish {

    private Long id;
    private Long orderId;
    private Long dishId;
    private Integer quantity;


}
