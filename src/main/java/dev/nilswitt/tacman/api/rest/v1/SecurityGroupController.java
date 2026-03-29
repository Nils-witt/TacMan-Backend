package dev.nilswitt.tacman.api.rest.v1;

import dev.nilswitt.tacman.api.dtos.SecurityGroupDto;
import dev.nilswitt.tacman.entities.SecurityGroup;
import dev.nilswitt.tacman.entities.User;
import dev.nilswitt.tacman.entities.repositories.SecurityGroupPermissionsRepository;
import dev.nilswitt.tacman.entities.repositories.SecurityGroupRepository;
import dev.nilswitt.tacman.exceptions.ForbiddenException;
import dev.nilswitt.tacman.exceptions.SecurityGroupNotFoundException;
import dev.nilswitt.tacman.security.PermissionVerifier;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("api/securitygroups")
public class SecurityGroupController {

    private final SecurityGroupRepository repository;
    private final SecurityGroupPermissionsRepository groupPermissionsRepository;
    private final SecurityGroupModelAssembler assembler;
    private final PermissionVerifier permissionVerifier;

    public SecurityGroupController(
            SecurityGroupRepository repository,
            SecurityGroupPermissionsRepository groupPermissionsRepository,
            SecurityGroupModelAssembler assembler,
            PermissionVerifier permissionVerifier
    ) {
        this.repository = repository;
        this.groupPermissionsRepository = groupPermissionsRepository;
        this.assembler = assembler;
        this.permissionVerifier = permissionVerifier;
    }

    @GetMapping("")
    CollectionModel<EntityModel<SecurityGroupDto>> all(
            @AuthenticationPrincipal User userDetails
    ) {
        if (
                !this.permissionVerifier.hasAccess(
                        userDetails,
                        SecurityGroup.UserRoleScopeEnum.VIEW,
                        SecurityGroup.UserRoleTypeEnum.SECURITYGROUP
                )
        ) {
            throw new ForbiddenException(
                    "User does not have permission to view security groups."
            );
        }

        List<EntityModel<SecurityGroupDto>> entities = this.repository.findAll()
                .stream()
                .map(group -> {
                    SecurityGroupDto dto = group.toDto();
                    dto.setPermissions(effectiveScopes(userDetails));
                    return dto;
                })
                .map(this.assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(
                entities,
                linkTo(methodOn(SecurityGroupController.class).all(null)).withSelfRel()
        );
    }

    @PostMapping("")
    EntityModel<SecurityGroupDto> newEntity(
            @RequestBody SecurityGroupDto newEntity,
            @AuthenticationPrincipal User userDetails
    ) {
        if (
                !this.permissionVerifier.hasAccess(
                        userDetails,
                        SecurityGroup.UserRoleScopeEnum.CREATE,
                        SecurityGroup.UserRoleTypeEnum.SECURITYGROUP
                )
        ) {
            throw new ForbiddenException(
                    "User does not have permission to create security groups."
            );
        }

        SecurityGroup group = new SecurityGroup();
        group.setName(newEntity.getName());
        group.setSsoGroupName(
                newEntity.getSsoGroupName() != null ? newEntity.getSsoGroupName() : ""
        );
        if (newEntity.getRoles() != null) {
            group.setRoles(Set.copyOf(newEntity.getRoles()));
        }

        SecurityGroup saved = this.repository.save(group);
        SecurityGroupDto dto = saved.toDto();
        dto.setPermissions(effectiveScopes(userDetails));
        return this.assembler.toModel(dto);
    }

    @GetMapping("{id}")
    EntityModel<SecurityGroupDto> one(
            @PathVariable UUID id,
            @AuthenticationPrincipal User userDetails
    ) {
        SecurityGroup entity = this.repository.findById(id).orElseThrow(() ->
                new SecurityGroupNotFoundException(id)
        );

        if (
                !this.permissionVerifier.hasAccess(
                        userDetails,
                        SecurityGroup.UserRoleScopeEnum.VIEW,
                        SecurityGroup.UserRoleTypeEnum.SECURITYGROUP
                )
        ) {
            throw new ForbiddenException(
                    "User does not have permission to view security groups."
            );
        }

        SecurityGroupDto dto = entity.toDto();
        dto.setPermissions(effectiveScopes(userDetails));
        return this.assembler.toModel(dto);
    }

    @PutMapping("{id}")
    EntityModel<SecurityGroupDto> replaceEntity(
            @RequestBody SecurityGroupDto newEntity,
            @PathVariable UUID id,
            @AuthenticationPrincipal User userDetails
    ) {
        SecurityGroup entity = this.repository.findById(id).orElseThrow(() ->
                new SecurityGroupNotFoundException(id)
        );

        if (
                !this.permissionVerifier.hasAccess(
                        userDetails,
                        SecurityGroup.UserRoleScopeEnum.EDIT,
                        SecurityGroup.UserRoleTypeEnum.SECURITYGROUP
                )
        ) {
            throw new ForbiddenException(
                    "User does not have permission to edit security groups."
            );
        }

        entity.setName(newEntity.getName());
        entity.setSsoGroupName(
                newEntity.getSsoGroupName() != null ? newEntity.getSsoGroupName() : ""
        );
        if (newEntity.getRoles() != null) {
            entity.setRoles(Set.copyOf(newEntity.getRoles()));
        }

        SecurityGroup saved = this.repository.save(entity);
        SecurityGroupDto dto = saved.toDto();
        dto.setPermissions(effectiveScopes(userDetails));
        return this.assembler.toModel(dto);
    }

    @DeleteMapping("{id}")
    @Transactional
    void deleteEntity(
            @PathVariable UUID id,
            @AuthenticationPrincipal User userDetails
    ) {
        SecurityGroup entity = this.repository.findById(id).orElseThrow(() ->
                new SecurityGroupNotFoundException(id)
        );

        if (
                !this.permissionVerifier.hasAccess(
                        userDetails,
                        SecurityGroup.UserRoleScopeEnum.DELETE,
                        SecurityGroup.UserRoleTypeEnum.SECURITYGROUP
                )
        ) {
            throw new ForbiddenException(
                    "User does not have permission to delete security groups."
            );
        }

        this.groupPermissionsRepository.deleteBySecurityGroup(entity);
        this.repository.removeFromAllUsers(id);
        this.repository.removeFromAllOverlays(id);
        this.repository.deleteById(id);
    }

    private Set<SecurityGroup.UserRoleScopeEnum> effectiveScopes(User user) {
        return EnumSet.allOf(SecurityGroup.UserRoleScopeEnum.class)
                .stream()
                .filter(scope ->
                        this.permissionVerifier.hasAccess(
                                user,
                                scope,
                                SecurityGroup.UserRoleTypeEnum.SECURITYGROUP
                        )
                )
                .collect(Collectors.toSet());
    }
}
