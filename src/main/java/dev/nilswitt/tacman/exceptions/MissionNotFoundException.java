package dev.nilswitt.tacman.exceptions;

import java.util.UUID;

public class MissionNotFoundException extends RuntimeException {

    public MissionNotFoundException(UUID id) {
        super("Could not find mission " + id);
    }
}
