package com.lucascosta.petapi.dto.resquest;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
        @NotBlank(message = "the field 'city' is required")
        String city,

        @NotBlank(message = "the field 'street' is required")
        String street,

        String number
) {
}
