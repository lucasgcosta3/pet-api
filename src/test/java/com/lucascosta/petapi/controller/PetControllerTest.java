package com.lucascosta.petapi.controller;

import com.lucascosta.petapi.commons.FileUtils;
import com.lucascosta.petapi.commons.PetUtils;
import com.lucascosta.petapi.dto.response.PetResponse;
import com.lucascosta.petapi.exception.PetNotFoundException;
import com.lucascosta.petapi.service.PetService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import com.lucascosta.petapi.exception.GlobalErrorHandlerAdvice;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@WebMvcTest(controllers = PetController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@Import({FileUtils.class, PetUtils.class, GlobalErrorHandlerAdvice.class})
class PetControllerTest {

    private static final String URL = "/v1/pets";
    private static final UUID NOT_FOUND_ID = UUID.fromString("99999999-9999-9999-9999-999999999999");

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private PetService service;
    @Autowired
    private FileUtils fileUtils;
    @Autowired
    private PetUtils petUtils;

    private PetResponse petResponse;
    private UUID petId;

    @BeforeEach
    void init() {
        petId = PetUtils.PET_ID_1;
        petResponse = petUtils.newPetResponse();
    }

    @Test
    @DisplayName("GET v1/pets returns a page with pets when type filter is provided")
    void findAll_ReturnsPageOfPets_WhenTypeFilterIsProvided() throws Exception {
        var page = new PageImpl<>(List.of(petResponse));
        BDDMockito.when(service.search(ArgumentMatchers.any(), ArgumentMatchers.any(Pageable.class))).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get(URL).param("type", "DOG"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("Rex Silva"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].type").value("DOG"));
    }

    @Test
    @DisplayName("GET v1/pets/{id} returns a pet with given id")
    void findById_ReturnsPetById_WhenSuccessful() throws Exception {
        var response = fileUtils.readResourceFile("pet/get-pet-by-id-200.json");
        BDDMockito.when(service.findById(petId)).thenReturn(petResponse);

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", petId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("GET v1/pets/{id} throws NotFound 404 when pet is not found")
    void findById_ThrowsNotFound_WhenPetIsNotFound() throws Exception {
        var response = fileUtils.readResourceFile("pet/get-pet-by-id-404.json");
        BDDMockito.when(service.findById(NOT_FOUND_ID))
                .thenThrow(new PetNotFoundException("Pet with id %s not found".formatted(NOT_FOUND_ID)));

        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/{id}", NOT_FOUND_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("POST v1/pets creates a pet")
    void save_CreatesPet_WhenSuccessful() throws Exception {
        var request = fileUtils.readResourceFile("pet/post-request-pet-200.json");
        var response = fileUtils.readResourceFile("pet/post-response-pet-201.json");

        BDDMockito.when(service.create(ArgumentMatchers.any())).thenReturn(petResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("PUT v1/pets/{id} updates a pet")
    void update_UpdatesPet_WhenSuccessful() throws Exception {
        var request = fileUtils.readResourceFile("pet/put-request-pet-200.json");
        BDDMockito.when(service.update(ArgumentMatchers.eq(petId), ArgumentMatchers.any())).thenReturn(petResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .put(URL + "/{id}", petId)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("PUT v1/pets/{id} throws NotFound when pet is not found")
    void update_ThrowsNotFound_WhenPetIsNotFound() throws Exception {
        var request = fileUtils.readResourceFile("pet/put-request-pet-404.json");
        var response = fileUtils.readResourceFile("pet/put-pet-by-id-404.json");

        BDDMockito.when(service.update(ArgumentMatchers.eq(NOT_FOUND_ID), ArgumentMatchers.any()))
                .thenThrow(new PetNotFoundException("Pet with id %s not found".formatted(NOT_FOUND_ID)));

        mockMvc.perform(MockMvcRequestBuilders
                        .put(URL + "/{id}", NOT_FOUND_ID)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @Test
    @DisplayName("DELETE v1/pets/{id} removes a pet")
    void delete_RemovePet_WhenSuccessful() throws Exception {
        BDDMockito.doNothing().when(service).delete(petId);

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", petId))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("DELETE v1/pets/{id} throws NotFound when pet is not found")
    void delete_ThrowsNotFound_WhenPetIsNotFound() throws Exception {
        var response = fileUtils.readResourceFile("pet/delete-pet-by-id-404.json");

        BDDMockito.doThrow(new PetNotFoundException("Pet with id %s not found".formatted(NOT_FOUND_ID)))
                .when(service).delete(NOT_FOUND_ID);

        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/{id}", NOT_FOUND_ID))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json(response));
    }

    @ParameterizedTest
    @MethodSource("postPetBadRequestSource")
    @DisplayName("POST v1/pets returns bad request when fields are invalid")
    void save_ReturnsBadRequest_WhenFieldsAreInvalid(String fileName, List<String> errors) throws Exception {
        var request = fileUtils.readResourceFile("pet/%s".formatted(fileName));

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        var resolvedException = mvcResult.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull();
        Assertions.assertThat(resolvedException.getMessage()).contains(errors);
    }

    @ParameterizedTest
    @MethodSource("putPetBadRequestSource")
    @DisplayName("PUT v1/pets/{id} returns bad request when fields are invalid")
    void update_ReturnsBadRequest_WhenFieldsAreInvalid(String fileName, List<String> errors) throws Exception {
        var request = fileUtils.readResourceFile("pet/%s".formatted(fileName));

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .put(URL + "/{id}", petId)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        var resolvedException = mvcResult.getResolvedException();

        Assertions.assertThat(resolvedException).isNotNull();
        Assertions.assertThat(resolvedException.getMessage()).contains(errors);
    }

    private static Stream<Arguments> postPetBadRequestSource() {
        var allRequiredErrors = allRequiredErrors();

        return Stream.of(
                Arguments.of("post-request-pet-empty-fields-400.json", allRequiredErrors),
                Arguments.of("post-request-pet-blank-fields-400.json", allRequiredErrors)
        );
    }

    private static Stream<Arguments> putPetBadRequestSource() {
        var invalidFieldsErrors = List.of("must be less than or equal to 60");

        return Stream.of(
                Arguments.of("put-request-pet-invalid-fields-400.json", invalidFieldsErrors)
        );
    }

    private static List<String> allRequiredErrors() {
        var nameRequired = "the field 'name' is required";
        var typeRequired = "the field 'type' is required";
        var genderRequired = "the field 'gender' is required";
        var birthDateRequired = "the field 'birthDate' is required";
        var weightRequired = "the field 'weight' is required";
        var breedRequired = "the field 'breed' is required";

        return new ArrayList<>(List.of(nameRequired, typeRequired, genderRequired, birthDateRequired, weightRequired, breedRequired));
    }
}
