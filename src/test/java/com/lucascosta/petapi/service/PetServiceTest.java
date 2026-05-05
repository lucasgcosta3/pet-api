package com.lucascosta.petapi.service;

import com.lucascosta.petapi.domain.pet.Pet;
import com.lucascosta.petapi.domain.pet.PetGender;
import com.lucascosta.petapi.domain.pet.PetType;
import com.lucascosta.petapi.domain.pet.Address;
import com.lucascosta.petapi.dto.request.AddressRequest;
import com.lucascosta.petapi.dto.request.PetFilterRequest;
import com.lucascosta.petapi.dto.request.PetPostRequest;
import com.lucascosta.petapi.dto.request.PetPutRequest;
import com.lucascosta.petapi.dto.response.AddressResponse;
import com.lucascosta.petapi.dto.response.PetResponse;
import com.lucascosta.petapi.exception.BusinessException;
import com.lucascosta.petapi.exception.PetNotFoundException;
import com.lucascosta.petapi.mapper.AddressMapper;
import com.lucascosta.petapi.mapper.PetMapper;
import com.lucascosta.petapi.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository repository;

    @Mock
    private PetMapper mapper;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private PetService service;

    private Pet pet;
    private PetResponse petResponse;
    private UUID petId;

    @BeforeEach
    void setUp() {
        petId = UUID.randomUUID();

        pet = Pet.builder()
                .id(petId)
                .name("Rex Silva")
                .type(PetType.DOG)
                .gender(PetGender.MALE)
                .address(Address.builder()
                        .city("Recife")
                        .street("Rua das Flores")
                        .number("123")
                        .build())
                .birthDate(LocalDate.now().minusYears(3))
                .weight(new BigDecimal("5.0"))
                .breed("Labrador")
                .createdAt(LocalDateTime.now())
                .build();

        petResponse = new PetResponse(
                petId,
                "Rex Silva",
                PetType.DOG,
                PetGender.MALE,
                new AddressResponse("Recife", "Rua das Flores", "123"),
                3,
                new BigDecimal("5.0"),
                "Labrador",
                LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("should create a pet successfully")
        void shouldCreatePetSuccessfully() {
            var request = new PetPostRequest(
                    "Rex Silva",
                    PetType.DOG,
                    PetGender.MALE,
                    new AddressRequest("Recife", "Rua das Flores", "123"),
                    LocalDate.now().minusYears(3),
                    new BigDecimal("5.0"),
                    "Labrador"
            );

            when(mapper.toEntity(any(PetPostRequest.class))).thenReturn(pet);
            when(repository.save(any(Pet.class))).thenReturn(pet);
            when(mapper.toResponse(pet)).thenReturn(petResponse);

            var result = service.create(request);

            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("Rex Silva");
            assertThat(result.type()).isEqualTo(PetType.DOG);
            verify(repository, times(1)).save(any(Pet.class));
        }

        @Test
        @DisplayName("should throw BusinessException when pet is older than 20 years")
        void shouldThrowWhenPetIsOlderThan20Years() {
            var request = new PetPostRequest(
                    "Rex Silva",
                    PetType.DOG,
                    PetGender.MALE,
                    new AddressRequest("Recife", "Rua das Flores", "123"),
                    LocalDate.now().minusYears(21),
                    new BigDecimal("5.0"),
                    "Labrador"
            );

            assertThatThrownBy(() -> service.create(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Pet should not be older than 20");

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("should fill NOT INFORMED for blank name")
        void shouldFillNotInformedForBlankName() {
            var request = new PetPostRequest(
                    "   ",
                    PetType.DOG,
                    PetGender.MALE,
                    new AddressRequest("Recife", "Rua das Flores", "123"),
                    LocalDate.now().minusYears(3),
                    new BigDecimal("5.0"),
                    "Labrador"
            );

            var petWithNotInformed = Pet.builder()
                    .id(petId).name("NOT INFORMED").type(PetType.DOG)
                    .gender(PetGender.MALE)
                    .address(Address.builder().city("Recife").street("Rua das Flores").number("123").build())
                    .birthDate(LocalDate.now().minusYears(3))
                    .weight(new BigDecimal("5.0")).breed("Labrador").build();

            when(mapper.toEntity(any(PetPostRequest.class))).thenReturn(petWithNotInformed);
            when(repository.save(any())).thenReturn(petWithNotInformed);
            when(mapper.toResponse(any())).thenReturn(new PetResponse(
                    petId, "NOT INFORMED", PetType.DOG, PetGender.MALE,
                    new AddressResponse("Recife", "Rua das Flores", "123"),
                    3, new BigDecimal("5.0"), "Labrador", LocalDateTime.now()
            ));

            var result = service.create(request);
            assertThat(result.name()).isEqualTo("NOT INFORMED");
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("should return pet when found")
        void shouldReturnPetWhenFound() {
            when(repository.findById(petId)).thenReturn(Optional.of(pet));
            when(mapper.toResponse(pet)).thenReturn(petResponse);

            var result = service.findById(petId);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(petId);
        }

        @Test
        @DisplayName("should throw PetNotFoundException when pet does not exist")
        void shouldThrowWhenPetNotFound() {
            var unknownId = UUID.randomUUID();
            when(repository.findById(unknownId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findById(unknownId))
                    .isInstanceOf(PetNotFoundException.class)
                    .hasMessageContaining(unknownId.toString());
        }
    }

    @Nested
    @DisplayName("update()")
    class Update {

        @Test
        @DisplayName("should update allowed fields")
        void shouldUpdateAllowedFields() {
            var updateRequest = new PetPutRequest(
                    "Rex Updated",
                    new AddressRequest("Olinda", "Rua Nova", "999"),
                    new BigDecimal("7.5")
            );

            var updatedAddress = Address.builder().city("Olinda").street("Rua Nova").number("999").build();

            when(repository.findById(petId)).thenReturn(Optional.of(pet));
            when(addressMapper.toAddress(any(AddressRequest.class))).thenReturn(updatedAddress);
            when(repository.save(pet)).thenReturn(pet);
            when(mapper.toResponse(pet)).thenReturn(petResponse);

            service.update(petId, updateRequest);

            assertThat(pet.getName()).isEqualTo("Rex Updated");
            assertThat(pet.getWeight()).isEqualByComparingTo("7.5");
            assertThat(pet.getAddress().getCity()).isEqualTo("Olinda");
            verify(repository).save(pet);
        }

        @Test
        @DisplayName("should not update fields when null")
        void shouldNotUpdateWhenFieldsAreNull() {
            var updateRequest = new PetPutRequest(null, null, null);

            when(repository.findById(petId)).thenReturn(Optional.of(pet));
            when(repository.save(pet)).thenReturn(pet);
            when(mapper.toResponse(pet)).thenReturn(petResponse);

            service.update(petId, updateRequest);

            assertThat(pet.getName()).isEqualTo("Rex Silva");
            assertThat(pet.getWeight()).isEqualByComparingTo("5.0");
            verify(addressMapper, never()).toAddress(any());
        }

        @Test
        @DisplayName("should throw PetNotFoundException when updating non-existent pet")
        void shouldThrowWhenUpdatingNonExistentPet() {
            var unknownId = UUID.randomUUID();
            when(repository.findById(unknownId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.update(unknownId, new PetPutRequest(null, null, null)))
                    .isInstanceOf(PetNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("should delete pet successfully")
        void shouldDeletePetSuccessfully() {
            when(repository.findById(petId)).thenReturn(Optional.of(pet));

            service.delete(petId);

            verify(repository, times(1)).delete(pet);
        }

        @Test
        @DisplayName("should throw PetNotFoundException when deleting non-existent pet")
        void shouldThrowWhenDeletingNonExistentPet() {
            var unknownId = UUID.randomUUID();
            when(repository.findById(unknownId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.delete(unknownId))
                    .isInstanceOf(PetNotFoundException.class)
                    .hasMessageContaining(unknownId.toString());

            verify(repository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("search()")
    class Search {

        @Test
        @DisplayName("should return page of pets matching filters")
        void shouldReturnPageOfPets() {
            var filter = new PetFilterRequest(PetType.DOG, "Rex", null, null, null, null, null, null, null);
            var pageable = PageRequest.of(0, 10);
            var page = new PageImpl<>(List.of(pet));

            when(repository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
            when(mapper.toResponse(pet)).thenReturn(petResponse);

            var result = service.search(filter, pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).name()).isEqualTo("Rex Silva");
        }

        @Test
        @DisplayName("should return empty page when no pets match")
        void shouldReturnEmptyPageWhenNoMatch() {
            var filter = new PetFilterRequest(PetType.CAT, null, null, null, null, null, null, null, null);
            var pageable = PageRequest.of(0, 10);

            when(repository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(new PageImpl<>(List.of()));

            var result = service.search(filter, pageable);

            assertThat(result.getContent()).isEmpty();
        }
    }
}
