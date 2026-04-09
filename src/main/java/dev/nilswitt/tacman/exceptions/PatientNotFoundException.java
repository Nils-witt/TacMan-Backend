package dev.nilswitt.tacman.exceptions;

import java.util.UUID;

public class PatientNotFoundException extends RuntimeException {

    public PatientNotFoundException(UUID id) {
        super("Could not find patient " + id);
    }
}
