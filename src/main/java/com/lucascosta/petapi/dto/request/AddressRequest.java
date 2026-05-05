package com.lucascosta.petapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
        @Schema(example = "Recife")
        @NotBlank(message = "O campo 'cidade' é obrigatório")
        String city,

        @Schema(example = "Rua das Pernambucanas")
        @NotBlank(message = "O campo 'rua' é obrigatório")
        String street,

        @Schema(example = "302")
        String number
) {
}
