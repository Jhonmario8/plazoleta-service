package com.pragma.plazoletaservice.infrastructure.output.feign;

import com.pragma.plazoletaservice.domain.model.OrderTraceabilityRequest;
import com.pragma.plazoletaservice.infrastructure.configuration.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "traceability-service", url = "${services.url-traceability}", configuration = FeignConfig.class)
public interface TraceabilityClient {

    @PostMapping("traceability/")
    void saveTraceabilityRecord(OrderTraceabilityRequest orderTraceabilityRequest);

    @GetMapping("traceability/{orderId}")
    OrderTraceabilityRequest findByOrderId(@PathVariable Long orderId);
}
