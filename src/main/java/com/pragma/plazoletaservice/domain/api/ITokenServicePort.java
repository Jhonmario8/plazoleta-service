package com.pragma.plazoletaservice.domain.api;

import java.util.Map;

public interface ITokenServicePort {
    Map<String, Object> validateToken(String token);
}
