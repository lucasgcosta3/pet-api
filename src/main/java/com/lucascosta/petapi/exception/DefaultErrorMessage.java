package com.lucascosta.petapi.exception;

import java.time.LocalDateTime;

public record DefaultErrorMessage(
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp
) {}
