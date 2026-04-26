package com.lucascosta.petapi.service;

import com.lucascosta.petapi.domain.Pet;
import com.lucascosta.petapi.dto.PetPostRequest;
import com.lucascosta.petapi.dto.PetPutRequest;
import com.lucascosta.petapi.dto.PetResponse;
import com.lucascosta.petapi.exception.PetNotFoundException;
import com.lucascosta.petapi.mapper.AddressMapper;
import com.lucascosta.petapi.mapper.PetMapper;
import com.lucascosta.petapi.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PetService {
    private final PetRepository repository;
    private final PetMapper mapper;
    private final AddressMapper addressMapper;

    public PetResponse create(PetPostRequest petPostRequest) {
        var pet = mapper.toEntity(petPostRequest);
        var savedPet = repository.save(pet);

        return mapper.toResponse(savedPet);
    }

    public Page<PetResponse> findAll(Pageable pageable) {
        Page<Pet> page = repository.findAll(pageable);
        return page.map(mapper::toResponse);
    }

    public PetResponse findById(UUID id) {
        var pet = getPetByIdOrThrow(id);
        return mapper.toResponse(pet);
    }

    public PetResponse update(UUID id, PetPutRequest request) {
        var petToUpdate = getPetByIdOrThrow(id);
        applyUpdates(petToUpdate, request);

        var petSaved = repository.save(petToUpdate);
        return mapper.toResponse(petSaved);
    }

    public void delete(UUID id) {
        var petToDelete = getPetByIdOrThrow(id);
        repository.delete(petToDelete);
    }

    public Page<PetResponse> findBy

    private Pet getPetByIdOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new PetNotFoundException("Pet with id %s not found"));
    }

    private void applyUpdates(Pet pet, PetPutRequest request) {

        if (request.name() != null && !request.name().isBlank()) {
            pet.setName(request.name());
        }

        if (request.address() != null) {
            pet.setAddress(addressMapper.toAddress(request.address()));
        }

        if (request.weight() != null) {
            pet.setWeight(request.weight());
        }
    }
}
