package com.pragma.plazoletaservice.infrastructure.output.jpa.repository;


import com.pragma.plazoletaservice.domain.model.OrderStatus;
import com.pragma.plazoletaservice.infrastructure.output.jpa.entites.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface IOrderRepository extends JpaRepository<OrderEntity, Long> {
    boolean existsByClientId(Long clientId);
    Page<OrderEntity> findAllByRestaurantIdAndStatus(Long restaurantId,OrderStatus status, Pageable pageable);
}
