package com.pragma.plazoletaservice.infrastructure.output.feign;

import com.pragma.plazoletaservice.domain.model.Sms;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class SmsServiceAdapterTest {

    @Mock
    private SmsClient smsClient;

    @InjectMocks
    private SmsServiceAdapter smsServiceAdapter;

    @Test
    @DisplayName("delega el SMS al cliente Feign de msg-service sin modificarlo")
    void delegatesToFeignClient() {
        // given
        Sms sms = new Sms("whatsapp:+573001234567", "Your order is ready for pickup!1234");

        // when
        smsServiceAdapter.sendSms(sms);

        // then
        verify(smsClient).sendSms(sms);
        verifyNoMoreInteractions(smsClient);
    }

    @Test
    @DisplayName("propaga la excepción si el cliente Feign falla")
    void propagatesFeignErrors() {
        // given
        Sms sms = new Sms("whatsapp:+573001234567", "msg");
        doThrow(new IllegalStateException("msg-service caído")).when(smsClient).sendSms(sms);

        // when / then
        assertThatThrownBy(() -> smsServiceAdapter.sendSms(sms))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("msg-service caído");
    }
}
