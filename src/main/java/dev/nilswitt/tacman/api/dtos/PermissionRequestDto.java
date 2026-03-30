package dev.nilswitt.tacman.api.dtos;

import dev.nilswitt.tacman.entities.SecurityGroup;
import java.util.UUID;

public record PermissionRequestDto(UUID subjectId, SecurityGroup.UserRoleScopeEnum scope) {}
