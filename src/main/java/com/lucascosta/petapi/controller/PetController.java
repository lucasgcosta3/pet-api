package com.lucascosta.petapi.controller;

import com.lucascosta.petapi.dto.response.PetResponse;
import com.lucascosta.petapi.dto.request.PetFilterRequest;
import com.lucascosta.petapi.dto.request.PetPostRequest;
import com.lucascosta.petapi.dto.request.PetPutRequest;
import com.lucascosta.petapi.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Pets", description = "Pet-related operations")
@RestController
@RequestMapping("v1/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService service;

    @Operation(summary = "Search pets with filters and pageable")
    @ApiResponse(responseCode = "200", description = "Pets found with success")
    @GetMapping
    public ResponseEntity<Page<PetResponse>> findAll(
            @ModelAttribute PetFilterRequest request,
            @ParameterObject Pageable pageable) {

        var pets = service.search(request, pageable);
        return ResponseEntity.ok(pets);
    }

    @Operation(summary = "Search pet by id")
    @ApiResponse(responseCode = "200", description = "Pet found with success")
    @GetMapping("/{id}")
    public ResponseEntity<PetResponse> findById(@PathVariable UUID id) {
        var pet = service.findById(id);
        return ResponseEntity.ok(pet);
    }

    @Operation(summary = "Creates a new pet")
    @ApiResponse(responseCode = "201", description = "Pet created with success")
    @PostMapping
    public ResponseEntity<PetResponse> create(@Valid @RequestBody PetPostRequest request) {
        var response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Updates an existent pet")
    @ApiResponse(responseCode = "200", description = "Pet updated with success")
    @PutMapping("/{id}")
    public ResponseEntity<PetResponse> update(@PathVariable UUID id, @Valid @RequestBody PetPutRequest request) {
        var response = service.update(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Removes a pet")
    @ApiResponse(responseCode = "204", description = "Pet deleted with success")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
