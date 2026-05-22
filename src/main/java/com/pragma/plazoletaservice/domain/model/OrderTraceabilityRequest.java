package com.pragma.plazoletaservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderTraceabilityRequest {

    private Long orderId;
    private Long clientId;
    private Long employeeId;
    private String previousState;
    private String newState;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long totalDurationInMinutes;

}
