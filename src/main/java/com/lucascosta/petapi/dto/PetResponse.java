package com.lucascosta.petapi.dto;

import com.lucascosta.petapi.domain.Address;
import com.lucascosta.petapi.domain.PetGender;
import com.lucascosta.petapi.domain.PetType;

import java.time.LocalDateTime;
import java.util.UUID;

public record PetResponse(
        UUID id,
        String name,
        PetType type,
        PetGender gender,
        Address address,
        Integer age,
        Double weight,
        String breed,
        LocalDateTime createdAt
){
}
