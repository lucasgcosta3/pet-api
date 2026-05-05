package com.lucascosta.petapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucascosta.petapi.domain.pet.PetGender;
import com.lucascosta.petapi.domain.pet.PetType;
import com.lucascosta.petapi.dto.request.AddressRequest;
import com.lucascosta.petapi.dto.request.PetPostRequest;
import com.lucascosta.petapi.dto.request.PetPutRequest;
import com.lucascosta.petapi.dto.response.AddressResponse;
import com.lucascosta.petapi.dto.response.PetResponse;
import com.lucascosta.petapi.exception.PetNotFoundException;
import com.lucascosta.petapi.service.PetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PetController.class)
class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PetService service;

    private PetResponse petResponse;
    private UUID petId;

    @BeforeEach
    void setUp() {
        petId = UUID.randomUUID();
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
    @DisplayName("GET /v1/pets")
    class FindAll {

        @Test
        @DisplayName("should return 200 with page of pets")
        void shouldReturn200WithPets() throws Exception {
            var page = new PageImpl<>(List.of(petResponse));
            when(service.search(any(), any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/v1/pets")
                            .param("type", "DOG")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].name").value("Rex Silva"))
                    .andExpect(jsonPath("$.content[0].type").value("DOG"));
        }
    }

    @Nested
    @DisplayName("GET /v1/pets/{id}")
    class FindById {

        @Test
        @DisplayName("should return 200 when pet exists")
        void shouldReturn200WhenPetExists() throws Exception {
            when(service.findById(petId)).thenReturn(petResponse);

            mockMvc.perform(get("/v1/pets/{id}", petId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(petId.toString()))
                    .andExpect(jsonPath("$.name").value("Rex Silva"));
        }

        @Test
        @DisplayName("should return 404 when pet does not exist")
        void shouldReturn404WhenPetNotFound() throws Exception {
            when(service.findById(any())).thenThrow(new PetNotFoundException("Pet not found"));

            mockMvc.perform(get("/v1/pets/{id}", UUID.randomUUID()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("Pet not found"));
        }
    }

    @Nested
    @DisplayName("POST /v1/pets")
    class Create {

        @Test
        @DisplayName("should return 201 when pet is created")
        void shouldReturn201WhenCreated() throws Exception {
            var request = new PetPostRequest(
                    "Rex Silva",
                    PetType.DOG,
                    PetGender.MALE,
                    new AddressRequest("Recife", "Rua das Flores", "123"),
                    LocalDate.now().minusYears(3),
                    new BigDecimal("5.0"),
                    "Labrador"
            );

            when(service.create(any())).thenReturn(petResponse);

            mockMvc.perform(post("/v1/pets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("Rex Silva"))
                    .andExpect(jsonPath("$.type").value("DOG"));
        }

        @Test
        @DisplayName("should return 400 when name is missing")
        void shouldReturn400WhenNameIsMissing() throws Exception {
            var request = new PetPostRequest(
                    null,
                    PetType.DOG,
                    PetGender.MALE,
                    new AddressRequest("Recife", "Rua das Flores", "123"),
                    LocalDate.now().minusYears(3),
                    new BigDecimal("5.0"),
                    "Labrador"
            );

            mockMvc.perform(post("/v1/pets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.name").exists());
        }

        @Test
        @DisplayName("should return 400 when weight exceeds 60kg")
        void shouldReturn400WhenWeightExceeds60kg() throws Exception {
            var request = new PetPostRequest(
                    "Rex Silva",
                    PetType.DOG,
                    PetGender.MALE,
                    new AddressRequest("Recife", "Rua das Flores", "123"),
                    LocalDate.now().minusYears(3),
                    new BigDecimal("61.0"),
                    "Labrador"
            );

            mockMvc.perform(post("/v1/pets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when name has no last name")
        void shouldReturn400WhenNameHasNoLastName() throws Exception {
            var request = new PetPostRequest(
                    "Rex",
                    PetType.DOG,
                    PetGender.MALE,
                    new AddressRequest("Recife", "Rua das Flores", "123"),
                    LocalDate.now().minusYears(3),
                    new BigDecimal("5.0"),
                    "Labrador"
            );

            mockMvc.perform(post("/v1/pets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /v1/pets/{id}")
    class Update {

        @Test
        @DisplayName("should return 200 when pet is updated")
        void shouldReturn200WhenUpdated() throws Exception {
            var request = new PetPutRequest("Rex Updated", null, new BigDecimal("7.0"));
            when(service.update(eq(petId), any())).thenReturn(petResponse);

            mockMvc.perform(put("/v1/pets/{id}", petId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("should return 404 when pet to update does not exist")
        void shouldReturn404WhenPetNotFound() throws Exception {
            var request = new PetPutRequest("Rex Updated", null, null);
            when(service.update(any(), any())).thenThrow(new PetNotFoundException("Pet not found"));

            mockMvc.perform(put("/v1/pets/{id}", UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /v1/pets/{id}")
    class DeletePet {

        @Test
        @DisplayName("should return 204 when pet is deleted")
        void shouldReturn204WhenDeleted() throws Exception {
            doNothing().when(service).delete(petId);

            mockMvc.perform(delete("/v1/pets/{id}", petId))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("should return 404 when pet to delete does not exist")
        void shouldReturn404WhenPetNotFound() throws Exception {
            doThrow(new PetNotFoundException("Pet not found")).when(service).delete(any());

            mockMvc.perform(delete("/v1/pets/{id}", UUID.randomUUID()))
                    .andExpect(status().isNotFound());
        }
    }
}
