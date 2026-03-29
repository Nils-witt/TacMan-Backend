package dev.nilswitt.tacman.api.dtos;

import dev.nilswitt.tacman.entities.SecurityGroup;

import java.util.UUID;

public record GroupPermissionEntryDto(
        UUID id,
        UUID groupId,
        String groupName,
        SecurityGroup.UserRoleScopeEnum scope
) {
}
