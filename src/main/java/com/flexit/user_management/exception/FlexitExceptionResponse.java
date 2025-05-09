package com.flexit.user_management.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FlexitExceptionResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime time;
    private HttpStatus status;
    private String error;
    private String description;

    public FlexitExceptionResponse() {
        this.time = LocalDateTime.now();
    }

    public FlexitExceptionResponse(HttpStatus status, String error, String description) {
        this();
        this.status = status;
        this.error =error;
        this.description=description;
    }
}