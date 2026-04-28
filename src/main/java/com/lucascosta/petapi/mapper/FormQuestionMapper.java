package com.lucascosta.petapi.mapper;

import com.lucascosta.petapi.domain.form.FormQuestion;
import com.lucascosta.petapi.dto.response.FormQuestionResponse;
import org.springframework.stereotype.Component;

@Component
public class FormQuestionMapper {

    public FormQuestionResponse toResponse(FormQuestion entity) {
        return new FormQuestionResponse(
                entity.getId(),
                entity.getNumber(),
                entity.getText()
        );
    }
}
