package dev.nilswitt.tacman.api.rest.v1;

import dev.nilswitt.tacman.api.dtos.UserDto;
import dev.nilswitt.tacman.entities.SecurityGroup;
import dev.nilswitt.tacman.entities.User;
import dev.nilswitt.tacman.entities.repositories.SecurityGroupRepository;
import dev.nilswitt.tacman.entities.repositories.UnitRepository;
import dev.nilswitt.tacman.entities.repositories.UserRepository;
import dev.nilswitt.tacman.exceptions.ForbiddenException;
import dev.nilswitt.tacman.exceptions.UserNotFoundException;
import dev.nilswitt.tacman.security.PermissionVerifier;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserRepository repository;
    private final UserModelAssembler assembler;
    private final PermissionVerifier permissionVerifier;
    private final UnitRepository unitRepository;
    private final SecurityGroupRepository securityGroupRepository;

    public UserController(
            UserRepository repository,
            UserModelAssembler assembler,
            PermissionVerifier permissionVerifier,
            UnitRepository unitRepository,
            SecurityGroupRepository securityGroupRepository
    ) {
        this.repository = repository;
        this.assembler = assembler;
        this.permissionVerifier = permissionVerifier;
        this.unitRepository = unitRepository;
        this.securityGroupRepository = securityGroupRepository;
    }

    @GetMapping("")
    public CollectionModel<EntityModel<UserDto>> all(
            @AuthenticationPrincipal User userDetails
    ) {
        if (
                this.permissionVerifier.hasAccess(
                        userDetails,
                        SecurityGroup.UserRoleScopeEnum.VIEW,
                        SecurityGroup.UserRoleTypeEnum.USER
                )
        ) {
            List<EntityModel<UserDto>> entities = this.repository.findAll()
                    .stream()
                    .map(user -> {
                        UserDto dto = user.toDto();
                        dto.setPermissions(
                                this.permissionVerifier.getScopes(user, userDetails)
                        );
                        return dto;
                    })
                    .map(this.assembler::toModel)
                    .collect(Collectors.toList());
            return CollectionModel.of(
                    entities,
                    linkTo(methodOn(UserController.class).all(null)).withSelfRel()
            );
        }

        return CollectionModel.of(
                this.permissionVerifier.getUsersForUser(userDetails)
                        .stream()
                        .map(user -> {
                            UserDto dto = user.toDto();
                            dto.setPermissions(
                                    this.permissionVerifier.getScopes(user, userDetails)
                            );
                            return dto;
                        })
                        .map(this.assembler::toModel)
                        .collect(Collectors.toList()),
                linkTo(methodOn(UserController.class).all(null)).withSelfRel()
        );
    }

    @PostMapping("")
    EntityModel<UserDto> newEmployee(
            @RequestBody UserDto newEntity,
            @AuthenticationPrincipal User userDetails
    ) {
        if (
                !this.permissionVerifier.hasAccess(
                        userDetails,
                        SecurityGroup.UserRoleScopeEnum.CREATE,
                        SecurityGroup.UserRoleTypeEnum.USER
                )
        ) {
            throw new ForbiddenException(
                    "User does not have permission to create overlays."
            );
        }
        User newUser = User.of(newEntity);
        securityGroupRepository
                .findByName("Everyone")
                .ifPresent(newUser::addSecurityGroup);
        newUser = this.repository.save(newUser);

        UserDto dto = newUser.toDto();
        dto.setPermissions(this.permissionVerifier.getScopes(newUser, userDetails));

        return this.assembler.toModel(dto);
    }

    @GetMapping("{id}")
    public EntityModel<UserDto> one(
            @PathVariable UUID id,
            @AuthenticationPrincipal User userDetails
    ) {
        User entity = this.repository.findById(id).orElseThrow(() ->
                new UserNotFoundException(id)
        );
        if (
                !this.permissionVerifier.hasAccess(
                        userDetails,
                        SecurityGroup.UserRoleScopeEnum.VIEW,
                        entity
                )
        ) {
            throw new ForbiddenException(
                    "User does not have permission to view users."
            );
        }

        UserDto dto = entity.toDto();
        dto.setPermissions(this.permissionVerifier.getScopes(entity, userDetails));
        return this.assembler.toModel(dto);
    }

    @PutMapping("{id}")
    EntityModel<UserDto> replaceEntity(
            @RequestBody UserDto newEntity,
            @PathVariable UUID id,
            @AuthenticationPrincipal User userDetails
    ) {
        User entity = this.repository.findById(id).orElseThrow(() ->
                new UserNotFoundException(id)
        );

        if (
                !this.permissionVerifier.hasAccess(
                        userDetails,
                        SecurityGroup.UserRoleScopeEnum.EDIT,
                        entity
                )
        ) {
            throw new ForbiddenException(
                    "User does not have permission to edit overlays."
            );
        }

        entity.setUsername(newEntity.getUsername());
        entity.setEmail(newEntity.getEmail());
        entity.setFirstName(newEntity.getFirstName());
        entity.setLastName(newEntity.getLastName());
        entity.setLocked(newEntity.isLocked());
        entity.setEnabled(newEntity.isEnabled());
        if (newEntity.getUnitId() != null) {
            entity.setUnit(
                    unitRepository.findById(newEntity.getUnitId()).orElse(null)
            );
        } else {
            entity.setUnit(null);
        }

        if (newEntity.getSecurityGroups() != null) {
            entity.setSecurityGroups(
                    newEntity
                            .getSecurityGroups()
                            .stream()
                            .map(uuid -> securityGroupRepository.findById(uuid).orElse(null))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet())
            );
        }
        User saved = this.repository.save(entity);

        UserDto dto = saved.toDto();
        dto.setPermissions(this.permissionVerifier.getScopes(saved, userDetails));
        return this.assembler.toModel(dto);
    }

    @DeleteMapping("{id}")
    void deleteEntity(
            @PathVariable UUID id,
            @AuthenticationPrincipal User userDetails
    ) {
        User entity = this.repository.findById(id).orElseThrow(() ->
                new UserNotFoundException(id)
        );

        if (
                !this.permissionVerifier.hasAccess(
                        userDetails,
                        SecurityGroup.UserRoleScopeEnum.DELETE,
                        entity
                )
        ) {
            throw new ForbiddenException(
                    "User does not have permission to delete overlays."
            );
        }
        this.repository.deleteById(id);
    }
}
