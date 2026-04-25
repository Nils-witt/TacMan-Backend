package dev.nilswitt.tacman.api.rest.advices;

import dev.nilswitt.tacman.exceptions.PatientNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class PatientNotFoundAdvice {

    @ExceptionHandler(PatientNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String patientNotFoundHandler(PatientNotFoundException ex) {
        return ex.getMessage();
    }
}
