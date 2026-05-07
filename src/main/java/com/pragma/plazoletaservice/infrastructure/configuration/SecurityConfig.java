package com.pragma.plazoletaservice.infrastructure.configuration;

import com.pragma.plazoletaservice.infrastructure.output.security.helper.CustomAuthenticationFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@AllArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationFilter customAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/restaurants").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/restaurants").authenticated()
                        .requestMatchers(HttpMethod.POST,  "/dishes").hasRole("OWNER")
                        .requestMatchers(HttpMethod.PUT, "/dishes").hasRole("OWNER")
                        .requestMatchers(HttpMethod.GET, "/orders").hasRole("EMPLOYEE")
                        .anyRequest().permitAll()
                );
        http.addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}