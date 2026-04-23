package com.pragma.plazoletaservice.infrastructure.output.jpa.adapter;


import com.pragma.plazoletaservice.domain.model.Dish;
import com.pragma.plazoletaservice.domain.spi.IDishPersistencePort;
import com.pragma.plazoletaservice.infrastructure.output.jpa.mapper.IDishEntityMapper;
import com.pragma.plazoletaservice.infrastructure.output.jpa.repository.IDishRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DishJpaAdapter implements IDishPersistencePort {

    private final IDishRepository dishRepository;
    private final IDishEntityMapper dishMapper;

    @Override
    public void saveDish(Dish dish) {
        dishRepository.save(dishMapper.toEntity(dish));
    }

    @Override
    public boolean existsDishByNameAndRestaurantId(String name, Long restaurantId) {
        return dishRepository.existsByNameAndRestaurantEntityId(name, restaurantId);
    }

    @Override
    public Optional<Dish> getDishById(Long id) {
        return dishRepository.findById(id).map(dishMapper::toDomain);
    }

}
