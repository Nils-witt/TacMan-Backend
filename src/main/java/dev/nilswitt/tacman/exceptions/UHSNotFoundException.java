package dev.nilswitt.tacman.exceptions;

import java.util.UUID;

public class UHSNotFoundException extends RuntimeException {

    public UHSNotFoundException(UUID id) {
        super("Could not find UHS " + id);
    }
}
