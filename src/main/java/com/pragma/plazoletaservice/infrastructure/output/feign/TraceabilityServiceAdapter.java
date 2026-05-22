package com.pragma.plazoletaservice.infrastructure.output.feign;

import com.pragma.plazoletaservice.domain.api.ITraceabilityServicePort;
import com.pragma.plazoletaservice.domain.model.OrderTraceabilityRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TraceabilityServiceAdapter implements ITraceabilityServicePort {

    private final TraceabilityClient traceabilityClient;

    @Override
    public void saveTraceabilityRecord(OrderTraceabilityRequest request) {
        traceabilityClient.saveTraceabilityRecord(request);
    }

    @Override
    public OrderTraceabilityRequest findTraceabilityById(Long orderId) {
        return traceabilityClient.findByOrderId(orderId);
    }

}
