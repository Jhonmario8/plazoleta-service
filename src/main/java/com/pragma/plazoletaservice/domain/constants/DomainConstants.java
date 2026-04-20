package com.pragma.plazoletaservice.domain.constants;

public class DomainConstants {

    private DomainConstants() {
    }
    public static final String ONLY_OWNER_CAN_CREATE_RESTAURANT_MESSAGE = "Only users with the OWNER role can create a restaurant";
    public static final String ROLE_NAME_CLAIM = "role_name";
    public static final String USER_ID_CLAIM = "user_id";
    public static final String MSG_OWNER_ID_INVALID = "Invalid owner ID";
    public static final String TOKEN_PREFIX = "Bearer ";
}
