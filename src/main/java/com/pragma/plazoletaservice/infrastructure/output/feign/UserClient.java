package com.pragma.plazoletaservice.infrastructure.output.feign;


import com.pragma.plazoletaservice.domain.model.Employee;
import com.pragma.plazoletaservice.domain.model.Role;
import com.pragma.plazoletaservice.infrastructure.configuration.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@FeignClient(name = "user-service", url = "${user-service.url}", configuration = FeignConfig.class)
public interface UserClient {

    @GetMapping("/users/{id}/role")
    Role getUserRole(@PathVariable Long id);

    @PostMapping("/users/employee")
    void createEmployee(Employee employee);

    @GetMapping("/users/{id}")
    Optional<Employee> getUserById(@PathVariable Long id);

}
