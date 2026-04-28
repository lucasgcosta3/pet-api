package com.lucascosta.petapi.service;

import com.lucascosta.petapi.domain.pet.Pet;
import com.lucascosta.petapi.dto.response.PetResponse;
import com.lucascosta.petapi.dto.request.AddressRequest;
import com.lucascosta.petapi.dto.request.PetFilterRequest;
import com.lucascosta.petapi.dto.request.PetPostRequest;
import com.lucascosta.petapi.dto.request.PetPutRequest;
import com.lucascosta.petapi.exception.BusinessException;
import com.lucascosta.petapi.exception.PetNotFoundException;
import com.lucascosta.petapi.mapper.AddressMapper;
import com.lucascosta.petapi.mapper.PetMapper;
import com.lucascosta.petapi.repository.PetRepository;
import com.lucascosta.petapi.repository.specification.PetSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PetService {
    private final PetRepository repository;
    private final PetMapper mapper;
    private final AddressMapper addressMapper;
    private static final String NOT_INFORMED = "NOT INFORMED";

    public PetResponse create(PetPostRequest petPostRequest) {
        var request = normalize(petPostRequest);

        var pet = mapper.toEntity(request);
        var savedPet = repository.save(pet);

        return mapper.toResponse(savedPet);
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

    public Page<PetResponse> search(PetFilterRequest request, Pageable pageable) {
        Specification<Pet> spec = buildSpecification(request);

        Page<Pet> pets = repository.findAll(spec, pageable);

        return pets.map(mapper::toResponse);
    }

    private Pet getPetByIdOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new PetNotFoundException(String.format("Pet with id %s not found", id)));
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

    private Specification<Pet> buildSpecification(PetFilterRequest request) {

        Specification<Pet> spec = PetSpecification.hasType(request.type());

        if (hasText(request.name())) {
            spec = spec.and(PetSpecification.hasName(request.name().trim()));
        }

        if (hasText(request.breed())) {
            spec = spec.and(PetSpecification.hasBreed(request.breed().trim()));
        }

        if (request.gender() != null) {
            spec = spec.and(PetSpecification.hasGender(request.gender()));
        }

        if (request.weight() != null) {
            spec = spec.and(PetSpecification.hasWeight(request.weight()));
        }

        if (request.age() != null) {
            spec = spec.and(PetSpecification.hasAge(request.age()));
        }

        if (request.city() != null) {
            spec = spec.and(PetSpecification.hasCity(request.city()));
        }

        if (request.street() != null) {
            spec = spec.and(PetSpecification.hasStreet(request.street()));
        }

        if (request.number() != null) {
            spec = spec.and(PetSpecification.hasNumber(request.number()));
        }

        return spec;
    }

    private boolean hasText(String value){
        return value != null && !value.trim().isEmpty();
    }

    private PetPostRequest normalize(PetPostRequest request){
        return new PetPostRequest(
                defaultIfBlank(request.name()),
                request.type(),
                request.gender(),
                normalizeAddress(request.address()),
                validateAge(request.birthDate()),
                request.weight(),
                defaultIfBlank(request.breed())
    );
    }

    private AddressRequest normalizeAddress(AddressRequest address){
        return new AddressRequest(
                defaultIfBlank(address.city()),
                defaultIfBlank(address.street()),
                address.number()
        );
    }

    private String defaultIfBlank(String value){
        return (value == null || value.isBlank())
                ? NOT_INFORMED
                : value;
    }

    private LocalDate validateAge(LocalDate birthDate){
        if(birthDate.isBefore(LocalDate.now().minusYears(20))){
            throw new BusinessException("Pet should not be older than 20");
        }
        return birthDate;
    }
}
