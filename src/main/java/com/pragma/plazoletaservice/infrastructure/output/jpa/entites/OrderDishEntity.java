package com.pragma.plazoletaservice.infrastructure.output.jpa.entites;

import com.pragma.plazoletaservice.infrastructure.constants.InfrastructureConstants;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @Column(nullable = false)
    private Long dishId;

    @NotNull(message = InfrastructureConstants.MSG_QUANTITY_NOT_NULL)
    private Integer quantity;
}
