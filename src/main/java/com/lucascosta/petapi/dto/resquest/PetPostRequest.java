package com.lucascosta.petapi.dto.resquest;

import com.lucascosta.petapi.domain.PetGender;
import com.lucascosta.petapi.domain.PetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record PetPostRequest(
        @NotBlank(message = "the field 'name' is required")
        String name,

        @NotNull(message = "the field 'type' is required")
        PetType type,

        @NotNull(message = "the field 'gender' is required")
        PetGender gender,

        AddressRequest address,

        @NotNull(message = "the field 'birthDate' is required")
        @PastOrPresent
        LocalDate birthDate,

        @NotNull(message = "the field 'weight' is required")
        Double weight,

        @NotBlank(message = "the field 'breed' is required")
        String breed
) {
}
