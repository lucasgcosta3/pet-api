package com.lucascosta.petapi.dto.request;

import com.lucascosta.petapi.domain.pet.PetGender;
import com.lucascosta.petapi.domain.pet.PetType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PetPostRequest(
        @Schema(example = "Rex Silva")
        @NotBlank(message = "O campo 'nome' é obrigatório")
        @Pattern(regexp = "^[A-Za-zÀ-ÿ]+(?:\\s+[A-Za-zÀ-ÿ]+)+$", message = "Informe o nome e sobrenome do pet")
        String name,

        @Schema(example = "DOG")
        @NotNull(message = "O campo 'tipo' é obrigatório")
        PetType type,

        @Schema(example = "MALE")
        @NotNull(message = "O campo 'gênero' é obrigatório")
        PetGender gender,

        @Schema(description = "Endereço onde o pet foi encontrado")
        @NotNull(message = "O endereço é obrigatório")
        AddressRequest address,

        @Schema(example = "2020-04-28")
        @NotNull(message = "O campo 'data de nascimento' é obrigatório")
        @PastOrPresent(message = "A data de nascimento não pode ser no futuro")
        LocalDate birthDate,

        @Schema(example = "5.0")
        @NotNull(message = "O campo 'peso' é obrigatório")
        @DecimalMin(value = "0.5", message = "O peso mínimo é 0.5 kg")
        @DecimalMax(value = "60", message = "O peso máximo é 60 kg")
        BigDecimal weight,

        @Schema(example = "Shitzu")
        @NotBlank(message = "O campo 'raça' é obrigatório")
        @Pattern(regexp = "^[A-Za-zÀ-ÿ ]+$", message = "A raça deve conter apenas letras")
        String breed,

        @Schema(description = "Base64 encoded photo string")
        String photoBase64
) {
}
