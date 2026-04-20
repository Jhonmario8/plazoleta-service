package com.pragma.plazoletaservice.infrastructure.util;

import com.pragma.plazoletaservice.domain.constants.DomainConstants;
import com.pragma.plazoletaservice.domain.model.Role;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

public class SecurityUtils {
    public static Long getCurrentUserId() {

        Map<String, Object> claims = getClaimsFromAuthentication();
        if (claims == null) {
            return null;
        }
        return Long.valueOf(claims.get(DomainConstants.USER_ID_CLAIM).toString());
    }

    public static Role getCurrentUserRole() {

        Map<String, Object> claims = getClaimsFromAuthentication();
        if (claims == null) {
            return null;
        }
        String roleName = claims.get(DomainConstants.ROLE_NAME_CLAIM).toString().toUpperCase();
        try {
            return Role.valueOf(roleName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static Map<String, Object> getClaimsFromAuthentication() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return (Map<String, Object>) authentication.getDetails();
    }
}
