package com.pragma.plazoletaservice.infrastructure.output.feign;

import com.pragma.plazoletaservice.domain.model.Role;
import com.pragma.plazoletaservice.infrastructure.configuration.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user-service.url}", configuration = FeignConfig.class)
public interface UserClient {

    @GetMapping("/users/{id}/role")
    Role getUserRole(@PathVariable Long id);
}
