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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true)
    private String nit;
    private String address;
    @Column(unique = true)
    private String phoneNumber;
    private String urlLogo;
    @Column(unique = true)
    private Long ownerId;


}
