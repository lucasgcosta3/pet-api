package com.lucascosta.petapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record PetPutRequest (
        @Schema(example = "Rex Silva")
        @Pattern(regexp = "^[A-Za-zÀ-ÿ]+(?:\\s+[A-Za-zÀ-ÿ]+)+$", message = "Informe o nome e sobrenome do pet")
        String name,

        AddressRequest address,

        @Schema(example = "5.0")
        @DecimalMin(value = "0.5", message = "O peso mínimo é 0.5 kg")
        @DecimalMax(value = "60", message = "O peso máximo é 60 kg")
        BigDecimal weight,

        @Schema(description = "Base64 encoded photo string")
        String photoBase64
){
}
