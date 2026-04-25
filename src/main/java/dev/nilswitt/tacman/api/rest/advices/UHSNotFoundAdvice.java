package dev.nilswitt.tacman.api.rest.advices;

import dev.nilswitt.tacman.exceptions.UHSNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class UHSNotFoundAdvice {

    @ExceptionHandler(UHSNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String uhsNotFoundHandler(UHSNotFoundException ex) {
        return ex.getMessage();
    }
}
