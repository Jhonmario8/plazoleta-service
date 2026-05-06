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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
     @Column(nullable = false)
    private String name;
     @Column(nullable = false)
    private Integer price;
     @Column(nullable = false, length = 500)
    private String description;
     @Column(nullable = false)
    private String urlImage;

    @ManyToOne
    private CategoryEntity category;
    private Boolean active;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private RestaurantEntity restaurantEntity;


}
