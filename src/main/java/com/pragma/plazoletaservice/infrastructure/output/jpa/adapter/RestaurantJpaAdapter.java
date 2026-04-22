package com.pragma.plazoletaservice.infrastructure.output.jpa.adapter;


import com.pragma.plazoletaservice.domain.model.Restaurant;
import com.pragma.plazoletaservice.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazoletaservice.infrastructure.output.jpa.entites.RestaurantEntity;
import com.pragma.plazoletaservice.infrastructure.output.jpa.mapper.IRestaurantEntityMapper;
import com.pragma.plazoletaservice.infrastructure.output.jpa.repository.IRestaurantRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class RestaurantJpaAdapter implements IRestaurantPersistencePort {

    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantMapper;

    @Override
    public void saveRestaurant(Restaurant restaurant){
        RestaurantEntity entity = restaurantMapper.toEntity(restaurant);
        restaurantRepository.save(entity);
    }
    @Override
    public Optional<Restaurant> getRestaurantById(Long id) {

       return restaurantRepository.findById(id).map(restaurantMapper::toDomain) ;
    }

    @Override
    public boolean existsByNit(String nit) {
        return restaurantRepository.existsByNit(nit);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return restaurantRepository.existsByPhoneNumber(phoneNumber);
    }
}
