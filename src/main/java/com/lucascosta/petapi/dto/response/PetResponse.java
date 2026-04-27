package com.lucascosta.petapi.dto.response;

import com.lucascosta.petapi.domain.PetGender;
import com.lucascosta.petapi.domain.PetType;

import java.time.LocalDateTime;
import java.util.UUID;

public record PetResponse(
        UUID id,
        String name,
        PetType type,
        PetGender gender,
        AddressResponse address,
        Integer age,
        Double weight,
        String breed,
        LocalDateTime createdAt
){
}
