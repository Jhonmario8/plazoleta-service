package com.pragma.plazoletaservice.infrastructure.output.jpa.entites;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "dishes",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_dish_name_restaurant",
                columnNames = {"name", "restaurant_id"}
        )
)
@Setter
@Getter
@NoArgsConstructor
public class DishEntity {

     @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer price;
    private String description;
    private String urlImage;
    private String category;
    private Boolean active;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private RestaurantEntity restaurantEntity;


}
