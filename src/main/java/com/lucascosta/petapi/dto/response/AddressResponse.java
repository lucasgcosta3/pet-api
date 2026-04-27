package com.lucascosta.petapi.dto.response;

public record AddressResponse(
        String city,
        String street,
        String number
) {
}
