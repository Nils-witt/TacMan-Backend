package dev.nilswitt.tacman.api.rest.advices;

import dev.nilswitt.tacman.exceptions.SecurityGroupNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class SecurityGroupNotFoundAdvice {

    @ExceptionHandler(SecurityGroupNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String securityGroupNotFoundHandler(SecurityGroupNotFoundException ex) {
        return ex.getMessage();
    }
}
