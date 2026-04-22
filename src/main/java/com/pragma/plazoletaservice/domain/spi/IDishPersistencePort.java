package com.pragma.plazoletaservice.domain.spi;

import com.pragma.plazoletaservice.domain.model.Dish;

public interface IDishPersistencePort {
    void saveDish(Dish dish);
}
