package dev.nilswitt.tacman.exceptions;

import java.util.UUID;

public class MapItemNotFoundException extends RuntimeException {

    public MapItemNotFoundException(UUID id) {
        super("Could not find item " + id);
    }
}