package dev.nilswitt.tacman.api.rest.advices;

import dev.nilswitt.tacman.exceptions.MissionGroupNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class MissionGroupNotFoundAdvice {

  @ExceptionHandler(MissionGroupNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  String missionNotFoundHandler(MissionGroupNotFoundException ex) {
    return ex.getMessage();
  }
}
