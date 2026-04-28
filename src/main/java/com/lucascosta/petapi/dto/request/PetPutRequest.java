package com.lucascosta.petapi.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record PetPutRequest (
        @Pattern(regexp = "^[A-Za-zÀ-ÿ]+(?:\\s+[A-Za-zÀ-ÿ]+)+$")
        String name,

        AddressRequest address,

        @DecimalMin("0.5")
        @DecimalMax("60")
        BigDecimal weight
){
}
