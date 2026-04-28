package com.lucascosta.petapi.dto.response;

import java.util.UUID;

public record FormQuestionResponse(
        UUID id,
        Integer number,
        String text
) {
}