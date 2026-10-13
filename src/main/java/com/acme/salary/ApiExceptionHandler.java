package com.acme.salary;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> invalidBody(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new LinkedHashMap<String, String>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler({IllegalArgumentException.class, ConstraintViolationException.class,
            HttpMessageNotReadableException.class})
    public ResponseEntity<Map<String, String>> badRequest(Exception exception) {
        return response(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> notFound(EntityNotFoundException exception) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler({DuplicateEmployeeException.class, DataIntegrityViolationException.class})
    public ResponseEntity<Map<String, String>> conflict(Exception exception) {
        String message = exception instanceof DataIntegrityViolationException
                ? "Employee number or email is already in use" : exception.getMessage();
        return response(HttpStatus.CONFLICT, message);
    }

    private ResponseEntity<Map<String, String>> response(HttpStatus status, String message) {
        Map<String, String> body = new LinkedHashMap<String, String>();
        body.put("error", message == null ? status.getReasonPhrase() : message);
        return ResponseEntity.status(status).body(body);
    }
}
