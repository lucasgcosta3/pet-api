package com.lucascosta.petapi.service;

import com.lucascosta.petapi.commons.PetUtils;
import com.lucascosta.petapi.domain.pet.Address;
import com.lucascosta.petapi.domain.pet.Pet;
import com.lucascosta.petapi.domain.pet.PetGender;
import com.lucascosta.petapi.domain.pet.PetType;
import com.lucascosta.petapi.dto.request.AddressRequest;
import com.lucascosta.petapi.dto.request.PetFilterRequest;
import com.lucascosta.petapi.dto.request.PetPostRequest;
import com.lucascosta.petapi.dto.request.PetPutRequest;
import com.lucascosta.petapi.dto.response.PetResponse;
import com.lucascosta.petapi.exception.BusinessException;
import com.lucascosta.petapi.exception.PetNotFoundException;
import com.lucascosta.petapi.mapper.AddressMapper;
import com.lucascosta.petapi.mapper.PetMapper;
import com.lucascosta.petapi.repository.PetRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @InjectMocks
    private PetService service;
    @Mock
    private PetRepository repository;
    @Mock
    private PetMapper mapper;
    @Mock
    private AddressMapper addressMapper;
    @Mock
    private GoogleSheetsService sheetsService;

    @InjectMocks
    private PetUtils petUtils;

    private List<Pet> petList;
    private PetResponse petResponse;

    @BeforeEach
    void init() {
        petList = petUtils.newPetList();
        petResponse = petUtils.newPetResponse();
    }

    @Test
    @DisplayName("findById returns a pet with given id")
    void findById_ReturnsPetById_WhenSuccessful() {
        var expectedPet = petList.getFirst();
        BDDMockito.when(repository.findById(expectedPet.getId())).thenReturn(Optional.of(expectedPet));
        BDDMockito.when(mapper.toResponse(expectedPet)).thenReturn(petResponse);

        var result = service.findById(expectedPet.getId());

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(expectedPet.getId());
    }

    @Test
    @DisplayName("findById throws PetNotFoundException when pet is not found")
    void findById_ThrowsPetNotFoundException_WhenPetIsNotFound() {
        var unknownId = UUID.randomUUID();
        BDDMockito.when(repository.findById(unknownId)).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.findById(unknownId))
                .isInstanceOf(PetNotFoundException.class);
    }

    @Test
    @DisplayName("create creates a pet")
    void create_CreatesPet_WhenSuccessful() {
        var petPostRequest = petUtils.newPetPostRequest();
        var petSaved = petUtils.newPetSaved();

        BDDMockito.when(mapper.toEntity(ArgumentMatchers.any(PetPostRequest.class))).thenReturn(petSaved);
        BDDMockito.when(repository.save(ArgumentMatchers.any(Pet.class))).thenReturn(petSaved);
        BDDMockito.when(mapper.toResponse(petSaved)).thenReturn(petResponse);

        var result = service.create(petPostRequest);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.name()).isEqualTo("Rex Silva");
        Assertions.assertThat(result.type()).isEqualTo(PetType.DOG);
    }

    @Test
    @DisplayName("create throws BusinessException when pet is older than 20 years")
    void create_ThrowsBusinessException_WhenPetIsOlderThan20Years() {
        var request = new PetPostRequest(
                "Rex Silva", PetType.DOG, PetGender.MALE,
                new AddressRequest("Recife", "Rua das Flores", "123"),
                LocalDate.now().minusYears(21), new BigDecimal("5.0"), "Labrador"
        );

        Assertions.assertThatException()
                .isThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessException.class)
                .withMessage("Pet should not be older than 20");
    }

    @Test
    @DisplayName("create fills NOT INFORMED for blank name")
    void create_FillsNotInformed_WhenNameIsBlank() {
        var request = new PetPostRequest(
                "   ", PetType.DOG, PetGender.MALE,
                new AddressRequest("Recife", "Rua das Flores", "123"),
                LocalDate.now().minusYears(3), new BigDecimal("5.0"), "Labrador"
        );

        var petSaved = petUtils.newPetSaved();
        petSaved.setName("NOT INFORMED");

        BDDMockito.when(mapper.toEntity(ArgumentMatchers.any(PetPostRequest.class))).thenReturn(petSaved);
        BDDMockito.when(repository.save(ArgumentMatchers.any())).thenReturn(petSaved);
        BDDMockito.when(mapper.toResponse(ArgumentMatchers.any())).thenReturn(
                new PetResponse(petSaved.getId(), "NOT INFORMED", PetType.DOG, PetGender.MALE,
                        petResponse.address(), 3, new BigDecimal("5.0"), "Labrador", petResponse.createdAt())
        );

        var result = service.create(request);

        Assertions.assertThat(result.name()).isEqualTo("NOT INFORMED");
    }

    @Test
    @DisplayName("update updates a pet")
    void update_UpdatesPet_WhenSuccessful() {
        var petToUpdate = petList.getFirst();
        var updateRequest = new PetPutRequest(
                "Rex Updated",
                new AddressRequest("Olinda", "Rua Nova", "999"),
                new BigDecimal("7.5")
        );
        var updatedAddress = Address.builder().city("Olinda").street("Rua Nova").number("999").build();

        BDDMockito.when(repository.findById(petToUpdate.getId())).thenReturn(Optional.of(petToUpdate));
        BDDMockito.when(addressMapper.toAddress(ArgumentMatchers.any(AddressRequest.class))).thenReturn(updatedAddress);
        BDDMockito.when(repository.save(petToUpdate)).thenReturn(petToUpdate);
        BDDMockito.when(mapper.toResponse(petToUpdate)).thenReturn(petResponse);

        Assertions.assertThatNoException().isThrownBy(() -> service.update(petToUpdate.getId(), updateRequest));

        Assertions.assertThat(petToUpdate.getName()).isEqualTo("Rex Updated");
        Assertions.assertThat(petToUpdate.getWeight()).isEqualByComparingTo("7.5");
        Assertions.assertThat(petToUpdate.getAddress().getCity()).isEqualTo("Olinda");
    }

    @Test
    @DisplayName("update does not modify fields when values are null")
    void update_DoesNotModifyFields_WhenValuesAreNull() {
        var petToUpdate = petList.getFirst();
        var originalName = petToUpdate.getName();
        var originalWeight = petToUpdate.getWeight();

        var updateRequest = new PetPutRequest(null, null, null);

        BDDMockito.when(repository.findById(petToUpdate.getId())).thenReturn(Optional.of(petToUpdate));
        BDDMockito.when(repository.save(petToUpdate)).thenReturn(petToUpdate);
        BDDMockito.when(mapper.toResponse(petToUpdate)).thenReturn(petResponse);

        service.update(petToUpdate.getId(), updateRequest);

        Assertions.assertThat(petToUpdate.getName()).isEqualTo(originalName);
        Assertions.assertThat(petToUpdate.getWeight()).isEqualByComparingTo(originalWeight);
    }

    @Test
    @DisplayName("update throws PetNotFoundException when pet is not found")
    void update_ThrowsPetNotFoundException_WhenPetIsNotFound() {
        var unknownId = UUID.randomUUID();
        BDDMockito.when(repository.findById(unknownId)).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.update(unknownId, new PetPutRequest(null, null, null)))
                .isInstanceOf(PetNotFoundException.class);
    }

    @Test
    @DisplayName("delete removes a pet")
    void delete_RemovePet_WhenSuccessful() {
        var petToDelete = petList.getFirst();
        BDDMockito.when(repository.findById(petToDelete.getId())).thenReturn(Optional.of(petToDelete));
        BDDMockito.doNothing().when(repository).delete(petToDelete);

        Assertions.assertThatNoException().isThrownBy(() -> service.delete(petToDelete.getId()));
    }

    @Test
    @DisplayName("delete throws PetNotFoundException when pet is not found")
    void delete_ThrowsPetNotFoundException_WhenPetIsNotFound() {
        var unknownId = UUID.randomUUID();
        BDDMockito.when(repository.findById(unknownId)).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> service.delete(unknownId))
                .isInstanceOf(PetNotFoundException.class);
    }

    @Test
    @DisplayName("search returns page of pets matching filters")
    void search_ReturnsPageOfPets_WhenFiltersMatch() {
        var filter = new PetFilterRequest(PetType.DOG, "Rex", null, null, null, null, null, null, null);
        var pageable = PageRequest.of(0, 10);
        var page = new PageImpl<>(List.of(petList.getFirst()));

        BDDMockito.when(repository.findAll(ArgumentMatchers.any(Specification.class), ArgumentMatchers.eq(pageable))).thenReturn(page);
        BDDMockito.when(mapper.toResponse(petList.getFirst())).thenReturn(petResponse);

        var result = service.search(filter, pageable);

        Assertions.assertThat(result.getContent()).hasSize(1);
        Assertions.assertThat(result.getContent().getFirst().name()).isEqualTo("Rex Silva");
    }

    @Test
    @DisplayName("search returns empty page when no pets match")
    void search_ReturnsEmptyPage_WhenNoPetsMatch() {
        var filter = new PetFilterRequest(PetType.CAT, null, null, null, null, null, null, null, null);
        var pageable = PageRequest.of(0, 10);

        BDDMockito.when(repository.findAll(ArgumentMatchers.any(Specification.class), ArgumentMatchers.eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        var result = service.search(filter, pageable);

        Assertions.assertThat(result.getContent()).isEmpty();
    }
}
