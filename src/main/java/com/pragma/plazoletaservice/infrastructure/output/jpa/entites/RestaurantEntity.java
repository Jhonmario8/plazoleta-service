package com.pragma.plazoletaservice.infrastructure.output.jpa.entites;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "restaurants")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String nit;
    private String address;
    private String phoneNumber;
    private String urlLogo;
    private Long ownerId;


}
