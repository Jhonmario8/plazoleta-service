package com.pragma.plazoletaservice.infrastructure.output.feign;


import com.pragma.plazoletaservice.domain.api.IUserServicePort;
import com.pragma.plazoletaservice.domain.model.Employee;
import com.pragma.plazoletaservice.domain.model.Role;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class UserServiceAdapter implements IUserServicePort {

    private final UserClient userClient;

    @Override
    public Role getUserRole(Long id) {
        return userClient.getUserRole(id);
    }

    @Override
    public void createEmployee(Employee employee) {
        userClient.createEmployee(employee);
    }

    @Override
    public Optional<Employee> getUserById(Long id) {
        return userClient.getUserById(id);
    }
}
