package com.pragma.plazoletaservice.infrastructure.output.jpa.entites;

import jakarta.persistence.*;

@Entity
@Table(name = "order_dish")
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
