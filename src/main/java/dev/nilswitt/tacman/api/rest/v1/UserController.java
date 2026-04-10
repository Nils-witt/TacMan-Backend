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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

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
    EntityModel<UserDto> newEmployee(@RequestBody UserPayload newEntity, @AuthenticationPrincipal User userDetails) {
        if (
            !this.permissionVerifier.hasAccess(
                userDetails,
                SecurityGroup.UserRoleScopeEnum.CREATE,
                SecurityGroup.UserRoleTypeEnum.USER
            )
        ) {
            throw new ForbiddenException("User does not have permission to create overlays.");
        }
        User newUser = new User(newEntity.username(), newEntity.email(), newEntity.firstName(), newEntity.lastName());
        if (newEntity.securityGroups() != null) {
            newEntity.securityGroups().forEach(groupId ->
                securityGroupService.findById(groupId).ifPresent(newUser::addSecurityGroup)
            );
        }
        securityGroupService.findByName("Everyone").ifPresent(newUser::addSecurityGroup);
        if (newEntity.unitId() != null) {
            newUser.setUnit(unitService.findById(newEntity.unitId()).orElse(null));
        }
        User saved = this.userService.save(newUser);
        return this.assembler.toModel(this.userService.toDto(saved, userDetails));
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
        @RequestBody UserPayload newEntity,
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
        entity.setUnit(
            newEntity.unitId() != null ? unitService.findById(newEntity.unitId()).orElse(null) : null
        );

        if (newEntity.securityGroups() != null) {
            entity.setSecurityGroups(
                newEntity.securityGroups()
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
                    List<UUID> securityGroupIds = mapper.convertValue(
                        data.get("securityGroups"),
                        mapper.getTypeFactory().constructCollectionType(List.class, UUID.class)
                    );
                    entity.setSecurityGroups(
                        new HashSet<>(
                            securityGroupIds
                                .stream()
                                .map(uuid -> securityGroupService.findById(uuid).orElse(null))
                                .filter(Objects::nonNull)
                                .toList()
                        )
                    );
                } catch (Exception e) {
                    log.error("Failed to parse securityGroups: {}", e.getMessage(), e);
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

    public record UserPayload(
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
