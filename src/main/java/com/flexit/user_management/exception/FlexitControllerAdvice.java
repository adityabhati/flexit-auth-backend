package com.flexit.user_management.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@RestControllerAdvice

public class FlexitControllerAdvice {

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<FlexitExceptionResponse> handleEverythingElse(Exception ex) {
        FlexitExceptionResponse response = new FlexitExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(FlexitCustomException.class)
    protected ResponseEntity<FlexitExceptionResponse> handleFlexitCustomException(FlexitCustomException ex) {
        HttpStatus httpStatus = (ex.getHttpStatus() == null) ? HttpStatus.INTERNAL_SERVER_ERROR : ex.getHttpStatus();
        FlexitExceptionResponse response = new FlexitExceptionResponse(httpStatus, ex.getError(), ex.getDescription());
        return ResponseEntity.status(httpStatus).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<FlexitExceptionResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String message =
                String.format(
                        "Parameter '%s' should be of type '%s'",
                        ex.getName(), Objects.requireNonNull(ex.getRequiredType()).getSimpleName());
        FlexitExceptionResponse response = new FlexitExceptionResponse(httpStatus, message, ex.getMessage());
        return ResponseEntity.status(httpStatus).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<FlexitExceptionResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
        String combinedMessages = String.join(", ", errorMessages);
        FlexitExceptionResponse response = new FlexitExceptionResponse(HttpStatus.BAD_REQUEST, "Validation Error", combinedMessages);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(SQLException.class)
    protected ResponseEntity<FlexitExceptionResponse> handleSqlException(SQLException ex) {
        FlexitExceptionResponse response = new FlexitExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, "SQL ERROR", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<FlexitExceptionResponse> handleDataIntegrityException(DataIntegrityViolationException ex) {
        FlexitExceptionResponse response = new FlexitExceptionResponse(HttpStatus.BAD_REQUEST, "Data integrity violation", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}