package com.pragma.plazoletaservice.infrastructure.output.feign;

import com.pragma.plazoletaservice.domain.model.Sms;
import com.pragma.plazoletaservice.infrastructure.configuration.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "sms-service", url = "${services.url-sms}", configuration = FeignConfig.class)
public interface SmsClient {

    @PostMapping("/sms/send")
    void sendSms(@RequestBody Sms sms);

}
