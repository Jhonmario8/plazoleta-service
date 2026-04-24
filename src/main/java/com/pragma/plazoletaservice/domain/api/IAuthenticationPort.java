package com.pragma.plazoletaservice.domain.api;

import com.pragma.plazoletaservice.domain.model.Role;

public interface IAuthenticationPort {
    Long getCurrentUserId();
    Role getCurrentUserRole();
}
