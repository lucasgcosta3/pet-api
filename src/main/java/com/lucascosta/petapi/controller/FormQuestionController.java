package com.lucascosta.petapi.controller;

import com.lucascosta.petapi.dto.response.FormQuestionResponse;
import com.lucascosta.petapi.service.FormQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Form", description = "Form questions for create pets")
@RestController
@RequestMapping("/v1/form-questions")
@RequiredArgsConstructor
public class FormQuestionController {

    private final FormQuestionService service;

    @Operation(summary = "Get form questions")
    @ApiResponse(responseCode = "200", description = "Questions found with success")
    @GetMapping
    public ResponseEntity<List<FormQuestionResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
}
