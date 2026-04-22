package com.lucascosta.petapi.service;

import com.lucascosta.petapi.domain.Pet;
import com.lucascosta.petapi.dto.PetPostRequest;
import com.lucascosta.petapi.dto.PetResponse;
import com.lucascosta.petapi.mapper.PetMapper;
import com.lucascosta.petapi.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PetService {
    private final PetRepository repository;
    private final PetMapper mapper;

    public PetResponse create(PetPostRequest petPostRequest) {
        Pet pet = mapper.toEntity(petPostRequest);
        Pet savedPet = repository.save(pet);

        return mapper.toResponse(savedPet);
    }

    public Page<PetResponse> findAll(Pageable pageable) {
        Page<Pet> page = repository.findAll(pageable);
        return page.map(mapper::toResponse);
    }
}
