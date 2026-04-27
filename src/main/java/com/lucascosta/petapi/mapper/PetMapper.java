package com.lucascosta.petapi.mapper;

import com.lucascosta.petapi.domain.Pet;
import com.lucascosta.petapi.dto.response.PetResponse;
import com.lucascosta.petapi.dto.resquest.PetPostRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PetMapper {

    Pet toEntity(PetPostRequest request);

    PetResponse toResponse(Pet pet);
}
