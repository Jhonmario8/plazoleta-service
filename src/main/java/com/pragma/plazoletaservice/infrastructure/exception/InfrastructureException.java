package com.pragma.plazoletaservice.infrastructure.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InfrastructureException extends RuntimeException{

    private final HttpStatus httpStatus;

    public InfrastructureException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
