package com.pragma.plazoletaservice.infrastructure.configuration;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor(){
        return requestTemplate -> {
            var authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null) {
                Object credentials = authentication.getCredentials();
                if (credentials != null) {
                    String token = credentials.toString();
                    requestTemplate.header("Authorization", "Bearer " + token);
                }
                Object details = authentication.getDetails();
                if (details instanceof java.util.Map) {
                    java.util.Map<String, Object> claims = (Map<String, Object>) details;
                    Object tokenObj = claims.get("token");
                    if (tokenObj != null) {
                        requestTemplate.header("Authorization", "Bearer " + tokenObj);
                    }
                }
            }
        };
    }
}
