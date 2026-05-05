package com.lucascosta.petapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
        @Schema(example = "Recife")
        @NotBlank(message = "the field 'city' is required")
        String city,

        @Schema(example = "Rua das Pernambucanas")
        @NotBlank(message = "the field 'street' is required")
        String street,

        @Schema(example = "302")
        String number
) {
}
