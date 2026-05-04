package com.pragma.plazoletaservice.infrastructure.output.jpa.adapter;


import com.pragma.plazoletaservice.domain.model.Dish;
import com.pragma.plazoletaservice.domain.spi.IDishPersistencePort;
import com.pragma.plazoletaservice.infrastructure.output.jpa.entites.DishEntity;
import com.pragma.plazoletaservice.infrastructure.output.jpa.mapper.IDishEntityMapper;
import com.pragma.plazoletaservice.infrastructure.output.jpa.repository.IDishRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Override
    public Page<Dish> getDishes(Long restaurantId, Long categoryId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<DishEntity> resultPage;

        if (categoryId != null) {
            resultPage = dishRepository.findByRestaurantEntityIdAndCategory_Id(restaurantId, categoryId, pageable);
        } else
            resultPage = dishRepository.findByRestaurantEntityId(restaurantId, pageable);

        return resultPage.map(dishMapper::toDomain);
    }

}
