package com.lucascosta.petapi.service;

import com.lucascosta.petapi.dto.response.FormQuestionResponse;
import com.lucascosta.petapi.mapper.FormQuestionMapper;
import com.lucascosta.petapi.repository.FormQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FormQuestionService {

    private final FormQuestionRepository repository;
    private final FormQuestionMapper mapper;

    public List<FormQuestionResponse> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "number"))
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}