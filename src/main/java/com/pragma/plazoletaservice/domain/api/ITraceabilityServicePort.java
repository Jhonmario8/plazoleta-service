package com.pragma.plazoletaservice.domain.api;

import com.pragma.plazoletaservice.domain.model.OrderTraceabilityRequest;

public interface ITraceabilityServicePort {
        void saveTraceabilityRecord(OrderTraceabilityRequest request);
        OrderTraceabilityRequest findTraceabilityById(Long id);
}
