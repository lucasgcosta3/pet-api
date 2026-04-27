package com.lucascosta.petapi.repository.specification;

import com.lucascosta.petapi.domain.Pet;
import com.lucascosta.petapi.domain.PetGender;
import com.lucascosta.petapi.domain.PetType;
import org.springframework.data.jpa.domain.Specification;

public class PetSpecification {

    public static Specification<Pet> hasType(PetType type) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("type"), type));
    }

    public static Specification<Pet> hasName(String name) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"
                ));
    }

    public static Specification<Pet> hasGender(PetGender gender) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("gender"), gender));
    }

    public static Specification<Pet> hasBreed(String breed) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("breed")),
                        "%" + breed.toLowerCase() + "%"
                ));
    }

    public static Specification<Pet> hasWeight(Double weight) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("weight"), weight));
    }
}
