package dev.nilswitt.tacman.api.rest.v1;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import dev.nilswitt.tacman.api.dtos.UserDto;
import dev.nilswitt.tacman.entities.SecurityGroup;
import dev.nilswitt.tacman.entities.User;
import dev.nilswitt.tacman.exceptions.ForbiddenException;
import dev.nilswitt.tacman.exceptions.UserNotFoundException;
import dev.nilswitt.tacman.security.PermissionVerifier;
import dev.nilswitt.tacman.services.SecurityGroupService;
import dev.nilswitt.tacman.services.UnitService;
import dev.nilswitt.tacman.services.UserService;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;

@Slf4j
@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;
    private final UserModelAssembler assembler;
    private final PermissionVerifier permissionVerifier;
    private final UnitService unitService;
    private final SecurityGroupService securityGroupService;
    private final PasswordEncoder passwordEncoder;

    public UserController(
        UserService userService,
        UserModelAssembler assembler,
        PermissionVerifier permissionVerifier,
        UnitService unitService,
        SecurityGroupService securityGroupService,
        PasswordEncoder passwordEncoder
    ) {
        this.userService = userService;
        this.assembler = assembler;
        this.permissionVerifier = permissionVerifier;
        this.unitService = unitService;
        this.securityGroupService = securityGroupService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("")
    public CollectionModel<EntityModel<UserDto>> all(@AuthenticationPrincipal User userDetails) {
        if (
            this.permissionVerifier.hasAccess(
                userDetails,
                SecurityGroup.UserRoleScopeEnum.VIEW,
                SecurityGroup.UserRoleTypeEnum.USER
            )
        ) {
            List<EntityModel<UserDto>> entities = this.userService.findAll()
                .stream()
                .map(user -> this.userService.toDto(user, userDetails))
                .map(this.assembler::toModel)
                .collect(Collectors.toList());
            return CollectionModel.of(entities, linkTo(methodOn(UserController.class).all(null)).withSelfRel());
        }

        return CollectionModel.of(
            this.permissionVerifier.getUsersForUser(userDetails)
                .stream()
                .map(user -> this.userService.toDto(user, userDetails))
                .map(this.assembler::toModel)
                .collect(Collectors.toList()),
            linkTo(methodOn(UserController.class).all(null)).withSelfRel()
        );
    }

    @PostMapping("")
    EntityModel<UserDto> newEmployee(
        @RequestBody UserCreatePayload newEntity,
        @AuthenticationPrincipal User userDetails
    ) {
        if (
            !this.permissionVerifier.hasAccess(
                userDetails,
                SecurityGroup.UserRoleScopeEnum.CREATE,
                SecurityGroup.UserRoleTypeEnum.USER
            )
        ) {
            throw new ForbiddenException("User does not have permission to create overlays.");
        }
        User newUser = new User();
        newUser.setEnabled(true);
        newUser.setLocked(false);
        newUser.setUsername(newEntity.username());
        newUser.setEmail(newEntity.email());
        newUser.setFirstName(newEntity.firstName());
        newUser.setLastName(newEntity.lastName());
        newUser.setUnit(newEntity.unitId() != null ? this.unitService.findById(newEntity.unitId()).orElse(null) : null);

        newUser = this.userService.save(newUser);

        UserDto dto = this.userService.toDto(newUser, userDetails);

        return this.assembler.toModel(dto);
    }

    @GetMapping("{id}")
    public EntityModel<UserDto> one(@PathVariable UUID id, @AuthenticationPrincipal User userDetails) {
        User entity = this.userService.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.VIEW, entity)) {
            throw new ForbiddenException("User does not have permission to view users.");
        }

        return this.assembler.toModel(this.userService.toDto(entity, userDetails));
    }

    @PutMapping("{id}")
    EntityModel<UserDto> replaceEntity(
        @RequestBody UserUpdatePayload newEntity,
        @PathVariable UUID id,
        @AuthenticationPrincipal User userDetails
    ) {
        User entity = this.userService.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.EDIT, entity)) {
            throw new ForbiddenException("User does not have permission to edit overlays.");
        }

        entity.setUsername(newEntity.username());
        entity.setEmail(newEntity.email());
        entity.setFirstName(newEntity.firstName());
        entity.setLastName(newEntity.lastName());
        entity.setLocked(newEntity.locked());
        entity.setEnabled(newEntity.enabled());
        if (newEntity.unitId() != null) {
            entity.setUnit(unitService.findById(newEntity.unitId()).orElse(null));
        } else {
            entity.setUnit(null);
        }

        if (newEntity.securityGroups() != null) {
            entity.setSecurityGroups(
                newEntity
                    .securityGroups()
                    .stream()
                    .map(uuid -> securityGroupService.findById(uuid).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet())
            );
        }
        User saved = this.userService.save(entity);

        return this.assembler.toModel(this.userService.toDto(saved, userDetails));
    }

    @PatchMapping("{id}")
    EntityModel<UserDto> updateEntity(
        @RequestBody String rawBody,
        @PathVariable UUID id,
        @AuthenticationPrincipal User userDetails
    ) {
        User entity = this.userService.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.EDIT, entity)) {
            throw new ForbiddenException("User does not have permission to edit overlays.");
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode data = mapper.readTree(rawBody);
            if (data.has("email")) {
                entity.setEmail(data.get("email").asString());
            }
            if (data.has("firstName")) {
                entity.setFirstName(data.get("firstName").asString());
            }
            if (data.has("lastName")) {
                entity.setLastName(data.get("lastName").asString());
            }
            if (data.has("locked")) {
                entity.setLocked(data.get("locked").asBoolean());
            }
            if (data.has("enabled")) {
                entity.setEnabled(data.get("enabled").asBoolean());
            }
            if (data.has("unitId")) {
                try {
                    String unitId = data.get("unitId").asString();
                    entity.setUnit(unitService.findById(UUID.fromString(unitId)).orElse(null));
                } catch (Exception e) {
                    entity.setUnit(null);
                }
            }
            if (data.has("securityGroups")) {
                try {
                    ArrayNode node = data.get("securityGroups").asArray();
                    Set<SecurityGroup> securityGroups = node
                        .elements()
                        .stream()
                        .map(JsonNode::asText)
                        .filter(Objects::nonNull)
                        .map(uuid -> {
                            try {
                                return securityGroupService.findById(UUID.fromString(uuid)).orElse(null);
                            } catch (Exception e) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
                    entity.setSecurityGroups(securityGroups);
                } catch (Exception e) {
                    entity.setSecurityGroups(new HashSet<>());
                    // ignore invalid security group ids
                }
            }
        } catch (Exception e) {
            log.error("Failed to parse JSON body: {}", e.getMessage(), e);
        }
        User saved = this.userService.save(entity);
        return this.assembler.toModel(this.userService.toDto(saved, userDetails));
    }

    @DeleteMapping("{id}")
    void deleteEntity(@PathVariable UUID id, @AuthenticationPrincipal User userDetails) {
        User entity = this.userService.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.DELETE, entity)) {
            throw new ForbiddenException("User does not have permission to delete overlays.");
        }
        this.userService.deleteById(id);
    }

    @PostMapping("{id}/password")
    EntityModel<UserDto> setPassword(
        @PathVariable UUID id,
        @RequestBody PasswordPayload body,
        @AuthenticationPrincipal User userDetails
    ) {
        User entity = this.userService.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        log.info("Changing password for user {}", entity.getUsername());
        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.EDIT, entity)) {
            throw new ForbiddenException("User does not have permission to set User Passwords.");
        }
        entity.setPassword(passwordEncoder.encode(body.password()));
        entity = this.userService.save(entity);

        return this.assembler.toModel(this.userService.toDto(entity, userDetails));
    }

    private record PasswordPayload(String password) {}

    private record UserCreatePayload(String username, String email, String firstName, String lastName, UUID unitId) {}

    private record UserUpdatePayload(
        String username,
        String email,
        String firstName,
        String lastName,
        boolean enabled,
        boolean locked,
        UUID unitId,
        Set<UUID> securityGroups
    ) {}
}
