package com.lucascosta.petapi.dto.resquest;

import com.lucascosta.petapi.domain.PetGender;
import com.lucascosta.petapi.domain.PetType;
import jakarta.validation.constraints.NotNull;

public record PetFilterRequest (
        @NotNull(message = "the field 'type' is required")
        PetType type,
        String name,
        PetGender gender,
        //Integer age
        Double weight,
        String breed
        //AddressRequest address
){
}
