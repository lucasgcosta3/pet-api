package com.lucascosta.petapi.dto.request;

import com.lucascosta.petapi.domain.pet.PetGender;
import com.lucascosta.petapi.domain.pet.PetType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PetFilterRequest (
        @NotNull(message = "the field 'type' is required")
        PetType type,
        String name,
        PetGender gender,
        Integer age,
        BigDecimal weight,
        String breed,
        String city,
        String street,
        String number
){
}
