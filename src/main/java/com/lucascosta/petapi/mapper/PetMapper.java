package com.lucascosta.petapi.mapper;

import com.lucascosta.petapi.domain.pet.Pet;
import com.lucascosta.petapi.dto.response.PetResponse;
import com.lucascosta.petapi.dto.request.PetPostRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PetMapper {

    Pet toEntity(PetPostRequest request);

    PetResponse toResponse(Pet pet);
}
