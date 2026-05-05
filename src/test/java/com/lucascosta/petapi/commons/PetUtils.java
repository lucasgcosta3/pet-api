package com.lucascosta.petapi.commons;

import com.lucascosta.petapi.domain.pet.Address;
import com.lucascosta.petapi.domain.pet.Pet;
import com.lucascosta.petapi.domain.pet.PetGender;
import com.lucascosta.petapi.domain.pet.PetType;
import com.lucascosta.petapi.dto.request.AddressRequest;
import com.lucascosta.petapi.dto.request.PetPostRequest;
import com.lucascosta.petapi.dto.response.AddressResponse;
import com.lucascosta.petapi.dto.response.PetResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class PetUtils {

    public static final UUID PET_ID_1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final UUID PET_ID_2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
    public static final UUID PET_ID_3 = UUID.fromString("33333333-3333-3333-3333-333333333333");
    public static final LocalDateTime CREATED_AT = LocalDateTime.of(2024, 1, 15, 10, 30, 0);

    public List<Pet> newPetList() {
        var rex = Pet.builder()
                .id(PET_ID_1).name("Rex Silva").type(PetType.DOG).gender(PetGender.MALE)
                .address(Address.builder().city("Recife").street("Rua das Flores").number("123").build())
                .birthDate(LocalDate.of(2021, 6, 15)).weight(new BigDecimal("5.0"))
                .breed("Labrador").createdAt(CREATED_AT).build();

        var mia = Pet.builder()
                .id(PET_ID_2).name("Mia Costa").type(PetType.CAT).gender(PetGender.FEMALE)
                .address(Address.builder().city("Olinda").street("Rua do Sol").number("456").build())
                .birthDate(LocalDate.of(2022, 3, 10)).weight(new BigDecimal("3.5"))
                .breed("Siamês").createdAt(CREATED_AT).build();

        var bob = Pet.builder()
                .id(PET_ID_3).name("Bob Santos").type(PetType.DOG).gender(PetGender.MALE)
                .address(Address.builder().city("Recife").street("Av Boa Viagem").number("789").build())
                .birthDate(LocalDate.of(2020, 1, 20)).weight(new BigDecimal("12.0"))
                .breed("Golden Retriever").createdAt(CREATED_AT).build();

        return new ArrayList<>(List.of(rex, mia, bob));
    }

    public Pet newPetSaved() {
        return newPetList().getFirst();
    }

    public Pet newPetToSave() {
        return Pet.builder()
                .name("Rex Silva").type(PetType.DOG).gender(PetGender.MALE)
                .address(Address.builder().city("Recife").street("Rua das Flores").number("123").build())
                .birthDate(LocalDate.of(2021, 6, 15)).weight(new BigDecimal("5.0"))
                .breed("Labrador").build();
    }

    public PetPostRequest newPetPostRequest() {
        return new PetPostRequest(
                "Rex Silva", PetType.DOG, PetGender.MALE,
                new AddressRequest("Recife", "Rua das Flores", "123"),
                LocalDate.of(2021, 6, 15), new BigDecimal("5.0"), "Labrador"
        );
    }

    public PetResponse newPetResponse() {
        return new PetResponse(
                PET_ID_1, "Rex Silva", PetType.DOG, PetGender.MALE,
                new AddressResponse("Recife", "Rua das Flores", "123"),
                3, new BigDecimal("5.0"), "Labrador", CREATED_AT
        );
    }
}
