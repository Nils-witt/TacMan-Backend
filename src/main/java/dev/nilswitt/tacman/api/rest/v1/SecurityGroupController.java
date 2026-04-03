package dev.nilswitt.tacman.api.rest.v1;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import dev.nilswitt.tacman.api.dtos.SecurityGroupDto;
import dev.nilswitt.tacman.entities.SecurityGroup;
import dev.nilswitt.tacman.entities.User;
import dev.nilswitt.tacman.exceptions.ForbiddenException;
import dev.nilswitt.tacman.exceptions.SecurityGroupNotFoundException;
import dev.nilswitt.tacman.security.PermissionVerifier;
import dev.nilswitt.tacman.services.SecurityGroupService;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/securitygroups")
public class SecurityGroupController {

    private final SecurityGroupService securityGroupService;
    private final SecurityGroupModelAssembler assembler;
    private final PermissionVerifier permissionVerifier;

    public SecurityGroupController(
        SecurityGroupService securityGroupService,
        SecurityGroupModelAssembler assembler,
        PermissionVerifier permissionVerifier
    ) {
        this.securityGroupService = securityGroupService;
        this.assembler = assembler;
        this.permissionVerifier = permissionVerifier;
    }

    @GetMapping("")
    CollectionModel<EntityModel<SecurityGroupDto>> all(@AuthenticationPrincipal User userDetails) {
        if (
            !this.permissionVerifier.hasAccess(
                userDetails,
                SecurityGroup.UserRoleScopeEnum.VIEW,
                SecurityGroup.UserRoleTypeEnum.SECURITYGROUP
            )
        ) {
            throw new ForbiddenException("User does not have permission to view security groups.");
        }

        List<EntityModel<SecurityGroupDto>> entities = this.securityGroupService.findAll()
            .stream()
            .map(group -> this.securityGroupService.toDto(group, userDetails))
            .map(this.assembler::toModel)
            .collect(Collectors.toList());

        return CollectionModel.of(entities, linkTo(methodOn(SecurityGroupController.class).all(null)).withSelfRel());
    }

    @PostMapping("")
    EntityModel<SecurityGroupDto> newEntity(
        @RequestBody SecurityGroupCreatePayload newEntity,
        @AuthenticationPrincipal User userDetails
    ) {
        if (
            !this.permissionVerifier.hasAccess(
                userDetails,
                SecurityGroup.UserRoleScopeEnum.CREATE,
                SecurityGroup.UserRoleTypeEnum.SECURITYGROUP
            )
        ) {
            throw new ForbiddenException("User does not have permission to create security groups.");
        }

        SecurityGroup entity = new SecurityGroup();
        entity.setName(newEntity.name());
        entity.setSsoGroupName(newEntity.ssoGroupName() != null ? newEntity.ssoGroupName() : "");
        if (newEntity.roles() != null) {
            entity.setRoles(
                newEntity.roles().stream().map(SecurityGroup.UserRoleScopeEnum::toString).collect(Collectors.toSet())
            );
        } else {
            entity.setRoles(Set.of());
        }

        entity = this.securityGroupService.save(entity);
        return this.assembler.toModel(this.securityGroupService.toDto(entity, userDetails));
    }

    @GetMapping("{id}")
    EntityModel<SecurityGroupDto> one(@PathVariable UUID id, @AuthenticationPrincipal User userDetails) {
        SecurityGroup entity = this.securityGroupService.findById(id).orElseThrow(() ->
            new SecurityGroupNotFoundException(id)
        );

        if (
            !this.permissionVerifier.hasAccess(
                userDetails,
                SecurityGroup.UserRoleScopeEnum.VIEW,
                SecurityGroup.UserRoleTypeEnum.SECURITYGROUP
            )
        ) {
            throw new ForbiddenException("User does not have permission to view security groups.");
        }

        return this.assembler.toModel(this.securityGroupService.toDto(entity, userDetails));
    }

    @PutMapping("{id}")
    EntityModel<SecurityGroupDto> replaceEntity(
        @RequestBody SecurityGroupCreatePayload newEntity,
        @PathVariable UUID id,
        @AuthenticationPrincipal User userDetails
    ) {
        SecurityGroup entity = this.securityGroupService.findById(id).orElseThrow(() ->
            new SecurityGroupNotFoundException(id)
        );

        if (
            !this.permissionVerifier.hasAccess(
                userDetails,
                SecurityGroup.UserRoleScopeEnum.EDIT,
                SecurityGroup.UserRoleTypeEnum.SECURITYGROUP
            )
        ) {
            throw new ForbiddenException("User does not have permission to edit security groups.");
        }

        entity.setName(newEntity.name());
        entity.setSsoGroupName(newEntity.ssoGroupName() != null ? newEntity.ssoGroupName() : "");
        if (newEntity.roles() != null) {
            entity.setRoles(
                newEntity.roles().stream().map(SecurityGroup.UserRoleScopeEnum::toString).collect(Collectors.toSet())
            );
        } else {
            entity.setRoles(Set.of());
        }

        entity = this.securityGroupService.save(entity);

        return this.assembler.toModel(this.securityGroupService.toDto(entity, userDetails));
    }

    @DeleteMapping("{id}")
    @Transactional
    void deleteEntity(@PathVariable UUID id, @AuthenticationPrincipal User userDetails) {
        SecurityGroup entity = this.securityGroupService.findById(id).orElseThrow(() ->
            new SecurityGroupNotFoundException(id)
        );

        if (
            !this.permissionVerifier.hasAccess(
                userDetails,
                SecurityGroup.UserRoleScopeEnum.DELETE,
                SecurityGroup.UserRoleTypeEnum.SECURITYGROUP
            )
        ) {
            throw new ForbiddenException("User does not have permission to delete security groups.");
        }

        this.securityGroupService.deletePermissionsBySecurityGroup(entity);
        this.securityGroupService.removeFromAllUsers(id);
        this.securityGroupService.removeFromAllOverlays(id);
        this.securityGroupService.deleteById(id);
    }

    public record SecurityGroupCreatePayload(
        String name,
        String ssoGroupName,
        Set<SecurityGroup.UserRoleScopeEnum> roles
    ) {}
}
