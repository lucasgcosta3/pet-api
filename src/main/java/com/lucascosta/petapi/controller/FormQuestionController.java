package com.lucascosta.petapi.controller;

import com.lucascosta.petapi.dto.response.FormQuestionResponse;
import com.lucascosta.petapi.service.FormQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/form-questions")
@RequiredArgsConstructor
public class FormQuestionController {

    private final FormQuestionService service;

    @GetMapping
    public ResponseEntity<List<FormQuestionResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
}
