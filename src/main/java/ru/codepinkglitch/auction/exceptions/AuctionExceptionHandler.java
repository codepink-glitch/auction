package ru.codepinkglitch.auction.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class AuctionExceptionHandler {

    @ExceptionHandler({ServiceException.class})
    public ResponseEntity<ApiError> handleGenericError(ServiceException exception, WebRequest request){
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, exception.getMessage(), exception.getExceptionEnum().name());
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }
}
