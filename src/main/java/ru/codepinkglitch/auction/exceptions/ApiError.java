package ru.codepinkglitch.auction.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

@Data
public class ApiError {

    private final HttpStatus status;
    private final String message;
    private final List<String> errors;

    public ApiError(HttpStatus status, String message, String error){
        this.status = status;
        this.message = message;
        errors = Collections.singletonList(error);
    }
}
