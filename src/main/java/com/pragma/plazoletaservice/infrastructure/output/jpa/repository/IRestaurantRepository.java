package com.pragma.plazoletaservice.infrastructure.output.jpa.repository;

import com.pragma.plazoletaservice.infrastructure.output.jpa.entites.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IRestaurantRepository extends JpaRepository<RestaurantEntity, Long> {

    boolean existsByNit(String nit);
    boolean existsByPhoneNumber(String phoneNumber);
}
