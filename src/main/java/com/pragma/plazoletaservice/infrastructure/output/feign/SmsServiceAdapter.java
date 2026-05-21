package com.pragma.plazoletaservice.infrastructure.output.feign;


import com.pragma.plazoletaservice.domain.api.ISmsServicePort;
import com.pragma.plazoletaservice.domain.model.Sms;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SmsServiceAdapter implements ISmsServicePort {

    private final SmsClient smsClient;

    @Override
    public void sendSms(Sms sms) {
       smsClient.sendSms(sms);
    }

}
