package dev.nilswitt.tacman.api.dtos;

import java.util.List;

public record EntityPermissionsDto(
    List<UserPermissionEntryDto> users,
    List<GroupPermissionEntryDto> groups
) {}
