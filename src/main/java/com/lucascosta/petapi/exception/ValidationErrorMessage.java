package com.lucascosta.petapi.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorMessage(
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp,
        Map<String, String> fields
) {}
