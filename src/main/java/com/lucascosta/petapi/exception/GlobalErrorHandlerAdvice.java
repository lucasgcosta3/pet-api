package com.lucascosta.petapi.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalErrorHandlerAdvice {

    private ResponseEntity<DefaultErrorMessage> buildError(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) {
        DefaultErrorMessage error = new DefaultErrorMessage(
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(PetNotFoundException.class)
    public ResponseEntity<DefaultErrorMessage> handlePetNotFoundException(
            PetNotFoundException e, HttpServletRequest request
    ) {
        return buildError(HttpStatus.NOT_FOUND, e.getMessage(), request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<DefaultErrorMessage> handleBusinessException(
            BusinessException e,
            HttpServletRequest request
    ) {
        return buildError(HttpStatus.BAD_REQUEST, e.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorMessage> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> fields = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                fields.put(error.getField(), error.getDefaultMessage())
        );

        int errorCount = fields.size();
        String summary = errorCount == 1
                ? "1 campo precisa de atenção"
                : errorCount + " campos precisam de atenção";

        ValidationErrorMessage error = new ValidationErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                summary,
                request.getRequestURI(),
                LocalDateTime.now(),
                fields
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DefaultErrorMessage> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno inesperado. Tente novamente mais tarde.",
                request
        );
    }
}
