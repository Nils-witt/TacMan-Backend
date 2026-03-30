package dev.nilswitt.tacman.api.dtos;

import dev.nilswitt.tacman.entities.SecurityGroup;
import java.util.UUID;

public record UserPermissionEntryDto(UUID id, UUID userId, String username, SecurityGroup.UserRoleScopeEnum scope) {}
