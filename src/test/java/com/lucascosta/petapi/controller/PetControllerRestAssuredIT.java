package com.lucascosta.petapi.controller;

import com.lucascosta.petapi.commons.FileUtils;
import com.lucascosta.petapi.commons.PetUtils;
import com.lucascosta.petapi.repository.PetRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import net.javacrumbs.jsonunit.core.Option;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PetControllerRestAssuredIT {

    private static final String URL = "/v1/pets";
    private static final UUID NOT_FOUND_ID = UUID.fromString("99999999-9999-9999-9999-999999999999");

    @Autowired
    private FileUtils fileUtils;
    @LocalServerPort
    private int port;

    @BeforeEach
    void setUrl() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    @DisplayName("GET v1/pets returns a page with pets when type filter is provided")
    @Sql(value = "/sql/pet/init_three_pets.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/pet/clean_pets.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAll_ReturnsPageOfPets_WhenTypeFilterIsProvided() {
        var response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .queryParam("type", "DOG")
                .when()
                .get(URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .node("content").isArray().isNotEmpty();
    }

    @Test
    @DisplayName("GET v1/pets/{id} returns a pet with given id")
    @Sql(value = "/sql/pet/init_one_pet.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/pet/clean_pets.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findById_ReturnsPetById_WhenSuccessful() {
        var expectedResponse = fileUtils.readResourceFile("pet/get-pet-by-id-200.json");
        var petId = PetUtils.PET_ID_1;

        var response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .pathParam("id", petId)
                .get(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("age", "createdAt", "weight")
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("GET v1/pets/{id} throws NotFound 404 when pet is not found")
    void findById_ThrowsNotFound_WhenPetIsNotFound() {
        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .pathParam("id", NOT_FOUND_ID)
                .get(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all();
    }

    @Test
    @DisplayName("POST v1/pets creates a pet")
    @Sql(value = "/sql/pet/clean_pets.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void save_CreatesPet_WhenSuccessful() {
        var request = fileUtils.readResourceFile("pet/post-request-pet-200.json");
        var expectedResponse = fileUtils.readResourceFile("pet/post-response-pet-201.json");

        var response = RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .body(request)
                .post(URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .log().all()
                .extract().response().body().asString();

        JsonAssertions.assertThatJson(response)
                .node("id").isNotNull();

        JsonAssertions.assertThatJson(response)
                .whenIgnoringPaths("id", "age", "createdAt")
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("DELETE v1/pets/{id} removes a pet")
    @Sql(value = "/sql/pet/init_one_pet.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/pet/clean_pets.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void delete_RemovePet_WhenSuccessful() {
        var petId = PetUtils.PET_ID_1;

        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .pathParam("id", petId)
                .delete(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .log().all();
    }

    @Test
    @DisplayName("DELETE v1/pets/{id} throws NotFound when pet is not found")
    void delete_ThrowsNotFound_WhenPetIsNotFound() {
        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .pathParam("id", NOT_FOUND_ID)
                .delete(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all();
    }

    @Test
    @DisplayName("PUT v1/pets/{id} updates a pet")
    @Sql(value = "/sql/pet/init_one_pet.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/pet/clean_pets.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void update_UpdatesPet_WhenSuccessful() {
        var request = fileUtils.readResourceFile("pet/put-request-pet-200.json");
        var petId = PetUtils.PET_ID_1;

        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .pathParam("id", petId)
                .body(request)
                .put(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all();
    }

    @Test
    @DisplayName("PUT v1/pets/{id} throws NotFound when pet is not found")
    void update_ThrowsNotFound_WhenPetIsNotFound() {
        var request = fileUtils.readResourceFile("pet/put-request-pet-404.json");

        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .pathParam("id", NOT_FOUND_ID)
                .body(request)
                .put(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all();
    }

    @Test
    @DisplayName("POST v1/pets returns bad request when fields are empty")
    void save_ReturnsBadRequest_WhenFieldsAreEmpty() {
        var request = fileUtils.readResourceFile("pet/post-request-pet-empty-fields-400.json");

        RestAssured.given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(request)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all();
    }
}
