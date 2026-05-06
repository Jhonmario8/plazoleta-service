package com.pragma.plazoletaservice.infrastructure.output.jpa.entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_dish")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDishEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @ManyToOne
    @JoinColumn(name = "dish_id")
    private DishEntity dish;

    private Integer quantity;
}
