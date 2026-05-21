package com.pragma.plazoletaservice.domain.api;

import com.pragma.plazoletaservice.domain.model.Sms;

public interface ISmsServicePort {
    void sendSms(Sms sms);
}
