package com.pragma.plazoletaservice.application.dto;

import com.pragma.plazoletaservice.application.constants.ApplicationConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantDto {

    private Long id;

    @NotBlank(message = ApplicationConstants.RESTAURANT_NAME_CANNOT_BE_BLANK_MESSAGE)
    @Pattern(regexp = "^(?!\\d+$).+", message = ApplicationConstants.RESTAURANT_NAME_ONLY_NUMBERS)
    private String name;

    @NotBlank(message = ApplicationConstants.NIT_CANNOT_BE_BLANK_MESSAGE)
    @Pattern(regexp = "^\\d+$", message = ApplicationConstants.NIT_ONLY_NUMBERS)
    private String nit;

    @NotBlank(message = ApplicationConstants.ADDRESS_CANNOT_BE_BLANK_MESSAGE)
    private String address;

    @NotBlank(message = ApplicationConstants.PHONE_NUMBER_CANNOT_BE_BLANK_MESSAGE)
    @Pattern(regexp = "^\\+?\\d{1,13}$", message = ApplicationConstants.PHONE_NUMBER_INVALID_FORMAT_MESSAGE)
    private String phoneNumber;

    @NotBlank(message = ApplicationConstants.LOGO_CANNOT_BE_BLANK_MESSAGE)
    private String urlLogo;


    private Long ownerId;
}
