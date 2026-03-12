package dev.nilswitt.webmap.api.exceptions;

import java.util.UUID;

public class MissionGroupNotFoundException extends RuntimeException {

    public MissionGroupNotFoundException(UUID id) {
        super("Could not find missionGroup " + id);
    }
}