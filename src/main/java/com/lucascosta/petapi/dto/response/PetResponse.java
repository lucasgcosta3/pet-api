package com.lucascosta.petapi.dto.response;

import com.lucascosta.petapi.domain.pet.PetGender;
import com.lucascosta.petapi.domain.pet.PetType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PetResponse(
        UUID id,
        String name,
        PetType type,
        PetGender gender,
        AddressResponse address,
        Integer age,
        BigDecimal weight,
        String breed,
        LocalDateTime createdAt
){
}
