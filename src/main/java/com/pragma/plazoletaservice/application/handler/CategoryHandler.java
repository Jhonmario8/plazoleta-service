package com.pragma.plazoletaservice.application.handler;

import com.pragma.plazoletaservice.domain.api.ICategoryServicePort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CategoryHandler implements ICategoryHandler{

        private final ICategoryServicePort categoryServicePort;

        @Override
        public void createCategory(String name) {
            categoryServicePort.createCategory(name);
        }
}
