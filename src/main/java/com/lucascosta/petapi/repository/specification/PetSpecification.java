package com.lucascosta.petapi.repository.specification;

import com.lucascosta.petapi.domain.pet.Pet;
import com.lucascosta.petapi.domain.pet.PetGender;
import com.lucascosta.petapi.domain.pet.PetType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    public static Specification<Pet> hasWeight(BigDecimal weight) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("weight"), weight));
    }

    public static Specification<Pet> hasAge(Integer age) {
        return (root, query, criteriaBuilder) -> {

            LocalDate today = LocalDate.now();

            LocalDate start = today.minusYears(age + 1).plusDays(1);
            LocalDate end   = today.minusYears(age);

            return criteriaBuilder.between(root.get("birthDate"), start, end);
        };
    }

    public static Specification<Pet> hasCity(String city) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("address").get("city")),
                        "%" + city.toLowerCase() + "%"
                );
    }

    public static Specification<Pet> hasStreet(String street) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("address").get("street")),
                        "%" + street.toLowerCase() + "%"
                );
    }

    public static Specification<Pet> hasNumber(String number) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("address").get("number"), number);
    }
}
