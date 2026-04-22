package com.pragma.plazoletaservice.infrastructure.output.jpa.repository;

import com.pragma.plazoletaservice.infrastructure.output.jpa.entites.DishEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDishRepository extends JpaRepository<DishEntity, Long> {
}
