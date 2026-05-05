package com.lucascosta.petapi.dto.request;

import com.lucascosta.petapi.domain.pet.PetGender;
import com.lucascosta.petapi.domain.pet.PetType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PetPostRequest(
        @Schema(example = "Rex Silva")
        @NotBlank(message = "the field 'name' is required")
        @Pattern(regexp = "^[A-Za-zÀ-ÿ]+(?:\\s+[A-Za-zÀ-ÿ]+)+$", message = "the pet should have name and last name")
        String name,

        @Schema(example = "DOG")
        @NotNull(message = "the field 'type' is required")
        PetType type,

        @Schema(example = "MALE")
        @NotNull(message = "the field 'gender' is required")
        PetGender gender,

        @Schema(description = "Endereço onde o pet foi encontrado")
        @NotNull
        AddressRequest address,

        @Schema(example = "2020-04-28")
        @NotNull(message = "the field 'birthDate' is required")
        @PastOrPresent
        LocalDate birthDate,

        @Schema(example = "5.0")
        @NotNull(message = "the field 'weight' is required")
        @DecimalMin("0.5")
        @DecimalMax("60")
        BigDecimal weight,

        @Schema(example = "Shitzu")
        @NotBlank(message = "the field 'breed' is required")
        @Pattern(regexp = "^[A-Za-zÀ-ÿ ]+$")
        String breed,

        @Schema(description = "Base64 encoded photo string")
        String photoBase64
) {
}
