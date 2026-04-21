package com.pragma.plazoletaservice.domain.api;

import com.pragma.plazoletaservice.domain.model.Role;

public interface IUserServicePort {
    Role getUserRole(Long id);
}
