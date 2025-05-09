package com.flexit.user_management.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class FlexitCustomException extends RuntimeException {

    private final String error;
    private String description;
    private HttpStatus httpStatus;


    public FlexitCustomException(String error) {
        this.error = error;
    }

    public FlexitCustomException(HttpStatus httpStatus, String error, String description) {
        this(error);
        this.httpStatus = httpStatus;
        this.description = description;
    }
}

