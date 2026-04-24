package com.pragma.plazoletaservice.infrastructure.output.security.adapter;

import com.pragma.plazoletaservice.domain.api.IAuthenticationPort;
import com.pragma.plazoletaservice.domain.model.Role;
import com.pragma.plazoletaservice.infrastructure.util.SecurityUtils;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationAdapter implements IAuthenticationPort {

    @Override
    public Long getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }
    @Override
    public Role getCurrentUserRole() {
        return SecurityUtils.getCurrentUserRole();
    }
}
