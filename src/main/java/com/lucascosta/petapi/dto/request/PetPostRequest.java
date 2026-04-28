package com.lucascosta.petapi.dto.request;

import com.lucascosta.petapi.domain.pet.PetGender;
import com.lucascosta.petapi.domain.pet.PetType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PetPostRequest(
        @NotBlank(message = "the field 'name' is required")
        @Pattern(regexp = "^[A-Za-zÀ-ÿ]+(?:\\s+[A-Za-zÀ-ÿ]+)+$", message = "the pet should have name and last name")
        String name,

        @NotNull(message = "the field 'type' is required")
        PetType type,

        @NotNull(message = "the field 'gender' is required")
        PetGender gender,

        @NotNull
        AddressRequest address,

        @NotNull(message = "the field 'birthDate' is required")
        @PastOrPresent
        LocalDate birthDate,

        @NotNull(message = "the field 'weight' is required")
        @DecimalMin("0.5")
        @DecimalMax("60")
        BigDecimal weight,

        @NotBlank(message = "the field 'breed' is required")
        @Pattern(regexp = "^[A-Za-zÀ-ÿ ]+$")
        String breed
) {
}
