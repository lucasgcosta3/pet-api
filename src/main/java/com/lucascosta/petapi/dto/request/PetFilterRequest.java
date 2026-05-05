package com.lucascosta.petapi.dto.request;

import com.lucascosta.petapi.domain.pet.PetGender;
import com.lucascosta.petapi.domain.pet.PetType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PetFilterRequest (
        @Schema(example = "DOG")
        PetType type,

        @Schema(example = "Rex")
        String name,

        @Schema(example = "MALE")
        PetGender gender,

        @Schema(example = "3")
        Integer age,

        @Schema(example = "5.0")
        BigDecimal weight,

        @Schema(example = "Shitzu")
        String breed,

        @Schema(example = "Recife")
        String city,

        @Schema(example = "Rua das Pernambucanas")
        String street,

        @Schema(example = "302")
        String number
){
}
