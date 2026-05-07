package com.pragma.plazoletaservice.domain.api;


import com.pragma.plazoletaservice.domain.model.Employee;
import com.pragma.plazoletaservice.domain.model.Role;

import java.util.Optional;

public interface IUserServicePort {
    Role getUserRole(Long id);
    void createEmployee(Employee employee);
    Optional<Employee> getUserById(Long id);
}
