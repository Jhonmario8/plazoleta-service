package com.pragma.plazoletaservice.domain.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Sms {

    private String destinationPhoneNumber;
    private String message;

}
