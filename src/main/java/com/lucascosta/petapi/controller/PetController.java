package com.lucascosta.petapi.controller;

import com.lucascosta.petapi.dto.response.PetResponse;
import com.lucascosta.petapi.dto.resquest.PetFilterRequest;
import com.lucascosta.petapi.mapper.PetMapper;
import com.lucascosta.petapi.service.PetService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("v1/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService service;
    private final PetMapper mapper;


    @GetMapping
    public ResponseEntity<Page<PetResponse>> findAll(Pageable pageable) {
        var pets = service.findAll(pageable);
        return ResponseEntity.ok(pets);
    }

    @GetMapping
    public ResponseEntity<Page<PetResponse>> search(@ModelAttribute PetFilterRequest request, Pageable pageable) {
        var petsFound = service.search(request, pageable);
        return ResponseEntity.ok(petsFound);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetResponse> findById(@PathVariable UUID id) {
        var pet = service.findById(id);
        return ResponseEntity.ok(pet);
    }


}
