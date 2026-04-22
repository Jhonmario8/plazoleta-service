package com.pragma.plazoletaservice.domain.constants;

public class DomainConstants {


    private DomainConstants() {
    }

    public static final String MSG_DISH_DESCRIPTION_CANNOT_BE_BLANK = "Description cannot be blank";
    public static final String MSG_DISH_CATEGORY_CANNOT_BE_BLANK = "Category cannot be blank";
    public static final String MSG_NAME_CANNOT_BE_BLANK = "Name cannot be blank";
    public static final String MSG_PRICE_INVALID = "Price must be a positive integer";
    public static final String MSG_OWNER_NOT_FOUND = "Owner not found";
    public static final String ONLY_OWNER_CAN_BE_ASSIGNED_MESSAGE = "Only users with the OWNER role can be assigned as restaurant owners";
    public static final String MSG_NAME_REQUIRED = "Name is required";
    public static final String MSG_NAME_ONLY_NUMBERS = "Name cannot be only numbers";
    public static final String MSG_NIT_MUST_BE_NUMERIC = "NIT must be numeric";
    public static final String MSG_INVALID_PHONE_NUMBER = "Invalid phone number";
    public static final String MSG_FIELD_REQUIRED = "%s is required";

    public static final String ROLE_NAME_CLAIM = "role_name";
    public static final String USER_ID_CLAIM = "user_id";
    public static final String MSG_OWNER_ID_INVALID = "Invalid owner ID";
    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String MSG_RESTAURANT_NOT_FOUND = "Restaurant not found";
    public static final String MSG_NOT_RESTAURANT_OWNER = "You are not the owner of this restaurant";
}