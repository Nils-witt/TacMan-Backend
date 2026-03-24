package dev.nilswitt.tacman.exceptions;

public class UnauthorizedException extends RuntimeException {

  public UnauthorizedException() {
    super("Not authorized to access resource ");
  }
}
