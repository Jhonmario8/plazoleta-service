package com.pragma.plazoletaservice.domain.model;

import com.pragma.plazoletaservice.domain.constants.DomainConstants;
import com.pragma.plazoletaservice.domain.exception.DomainException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dish {
    private Long id;
    private String name;
    private Integer price;
    private String description;
    private String urlImage;
    private String category;
    private Boolean active;
    private Restaurant restaurant;




    public Dish(Long id, String name,Integer price, String description, String urlImage,String category, Restaurant restaurant) {

        validatePrice(price);
        validateName(name);
        validateDescription(description);
        validateCategory(category);
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.urlImage = urlImage;
        this.category = category;
        this.active = true;
        this.restaurant = restaurant;

    }

    private void validatePrice(Integer price) {
        if (price == null || price <= 0) {
            throw new DomainException(DomainConstants.MSG_PRICE_INVALID);
        }
    }
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new DomainException(DomainConstants.MSG_NAME_CANNOT_BE_BLANK);
        }
    }
    private void validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new DomainException(DomainConstants.MSG_DISH_DESCRIPTION_CANNOT_BE_BLANK);
        }
    }

    private void validateCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new DomainException(DomainConstants.MSG_DISH_CATEGORY_CANNOT_BE_BLANK);
        }
    }


}
