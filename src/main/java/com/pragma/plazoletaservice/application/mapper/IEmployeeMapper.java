package com.pragma.plazoletaservice.application.mapper;

import com.pragma.plazoletaservice.application.dto.EmployeeRequestDTO;
import com.pragma.plazoletaservice.domain.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IEmployeeMapper {

    Employee toDomain(EmployeeRequestDTO dto);


}
