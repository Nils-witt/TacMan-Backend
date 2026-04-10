package dev.nilswitt.tacman.api.rest.v1;

import dev.nilswitt.tacman.api.PermissionManagementService;
import dev.nilswitt.tacman.api.dtos.EntityPermissionsDto;
import dev.nilswitt.tacman.entities.AbstractEntity;
import dev.nilswitt.tacman.entities.SecurityGroup;
import dev.nilswitt.tacman.entities.User;
import dev.nilswitt.tacman.exceptions.ForbiddenException;
import dev.nilswitt.tacman.security.PermissionVerifier;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/permissions/{entityType}/{entityId}")
public class PermissionController {

    private final PermissionManagementService service;
    private final PermissionVerifier permissionVerifier;

    public PermissionController(PermissionManagementService service, PermissionVerifier permissionVerifier) {
        this.service = service;
        this.permissionVerifier = permissionVerifier;
    }

    @GetMapping("")
    public EntityPermissionsDto getPermissions(
        @PathVariable String entityType,
        @PathVariable UUID entityId,
        @AuthenticationPrincipal User currentUser
    ) {
        AbstractEntity entity = service.findEntity(entityType, entityId);
        if (!permissionVerifier.getScopes(entity, currentUser).contains(SecurityGroup.UserRoleScopeEnum.VIEW)) {
            throw new ForbiddenException("Access denied.");
        }
        return service.getPermissions(entity);
    }

    @PostMapping("users")
    public void grantUserPermission(
        @PathVariable String entityType,
        @PathVariable UUID entityId,
        @RequestBody PermissionPayload request,
        @AuthenticationPrincipal User currentUser
    ) {
        requireAdmin(currentUser, entityType);
        AbstractEntity entity = service.findEntity(entityType, entityId);
        service.grantUserPermission(entity, request.subjectId(), request.scope());
    }

    @DeleteMapping("users/{userId}")
    public void revokeUserPermission(
        @PathVariable String entityType,
        @PathVariable UUID entityId,
        @PathVariable UUID userId,
        @AuthenticationPrincipal User currentUser
    ) {
        requireAdmin(currentUser, entityType);
        AbstractEntity entity = service.findEntity(entityType, entityId);
        service.revokeUserPermission(entity, userId);
    }

    @PostMapping("groups")
    public void grantGroupPermission(
        @PathVariable String entityType,
        @PathVariable UUID entityId,
        @RequestBody PermissionPayload request,
        @AuthenticationPrincipal User currentUser
    ) {
        requireAdmin(currentUser, entityType);
        AbstractEntity entity = service.findEntity(entityType, entityId);
        service.grantGroupPermission(entity, request.subjectId(), request.scope());
    }

    @DeleteMapping("groups/{groupId}")
    public void revokeGroupPermission(
        @PathVariable String entityType,
        @PathVariable UUID entityId,
        @PathVariable UUID groupId,
        @AuthenticationPrincipal User currentUser
    ) {
        requireAdmin(currentUser, entityType);
        AbstractEntity entity = service.findEntity(entityType, entityId);
        service.revokeGroupPermission(entity, groupId);
    }

    private void requireAdmin(User user, String entityType) {
        SecurityGroup.UserRoleTypeEnum type = service.resolveRoleType(entityType);
        if (!permissionVerifier.hasAccess(user, SecurityGroup.UserRoleScopeEnum.ADMIN, type)) {
            throw new ForbiddenException("Admin access required to manage permissions for " + entityType + ".");
        }
    }

    public record PermissionPayload(UUID subjectId, SecurityGroup.UserRoleScopeEnum scope) {}
}
