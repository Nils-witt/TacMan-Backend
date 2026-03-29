package dev.nilswitt.tacman.exceptions;

import java.util.UUID;

public class SecurityGroupNotFoundException extends RuntimeException {

    public SecurityGroupNotFoundException(UUID id) {
        super("Could not find security group " + id);
    }
}
