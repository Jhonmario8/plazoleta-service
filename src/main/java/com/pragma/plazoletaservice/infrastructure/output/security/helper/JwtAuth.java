package com.pragma.plazoletaservice.infrastructure.output.security.helper;


import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class JwtAuth implements Authentication {

    private final Map<String, Object> claims;
    private final String token;

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Object roleName = claims.get("role_name");
        if (roleName == null) {
            return Collections.emptyList();
        }
        String role = String.valueOf(roleName);
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }


    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getDetails() {
        return claims;
    }

    @Override
    public Object getPrincipal() {
        return claims.get("sub");
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new UnsupportedOperationException("No se puede cambiar el estado de autenticación");
    }

    @Override
    public String getName() {
        return (String) claims.get("sub");
    }
}
