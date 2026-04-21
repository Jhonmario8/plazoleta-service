package com.pragma.plazoletaservice.domain.model;

import com.pragma.plazoletaservice.domain.constants.DomainConstants;
import com.pragma.plazoletaservice.domain.exception.DomainException;

import lombok.Data;


@Data
public class Restaurant {

    private Long id;
    private String name;
    private String nit;
    private String address;
    private String phoneNumber;
    private String urlLogo;
    private Long ownerId;

    public Restaurant(Long id, String name, String nit, String address,
                      String phoneNumber, String urlLogo, Long ownerId) {

        validateName(name);
        validateNit(nit);
        validatePhone(phoneNumber);
        validateNotEmpty(address, "Address");
        validateNotEmpty(urlLogo, "UrlLogo");

        this.id = id;
        this.name = name;
        this.nit = nit;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.urlLogo = urlLogo;
        assignOwner(ownerId);
    }

    private void assignOwner(Long ownerId) {
        if (ownerId == null || ownerId <= 0) {
            throw new DomainException(DomainConstants.MSG_OWNER_ID_INVALID);
        }
        this.ownerId = ownerId;
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new DomainException(DomainConstants.MSG_NAME_REQUIRED);
        }
        if (name.matches("\\d+")) {
            throw new DomainException(DomainConstants.MSG_NAME_ONLY_NUMBERS);
        }
    }

    private void validateNit(String nit) {
        if (nit == null || !nit.matches("\\d+")) {
            throw new DomainException(DomainConstants.MSG_NIT_MUST_BE_NUMERIC);
        }
    }

    private void validatePhone(String phone) {
        if (phone == null || !phone.matches("^\\+?\\d{1,13}$")) {
            throw new DomainException(DomainConstants.MSG_INVALID_PHONE_NUMBER);
        }
    }

    private void validateNotEmpty(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainException(String.format(DomainConstants.MSG_FIELD_REQUIRED, field));
        }
    }

}
