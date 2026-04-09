package dev.nilswitt.tacman.api.rest.v1;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import dev.nilswitt.tacman.api.dtos.EmbeddedPositionDto;
import dev.nilswitt.tacman.api.dtos.UHSDto;
import dev.nilswitt.tacman.entities.EmbeddedPosition;
import dev.nilswitt.tacman.entities.SecurityGroup;
import dev.nilswitt.tacman.entities.UHS;
import dev.nilswitt.tacman.entities.User;
import dev.nilswitt.tacman.entities.repositories.MissionGroupRepository;
import dev.nilswitt.tacman.entities.repositories.UserRepository;
import dev.nilswitt.tacman.exceptions.ForbiddenException;
import dev.nilswitt.tacman.exceptions.UHSNotFoundException;
import dev.nilswitt.tacman.security.PermissionVerifier;
import dev.nilswitt.tacman.services.UHSService;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/uhs")
public class UHSController {

    private final UHSService uhsService;
    private final UHSModelAssembler assembler;
    private final PermissionVerifier permissionVerifier;
    private final UserRepository userRepository;
    private final MissionGroupRepository missionGroupRepository;

    public UHSController(
        UHSService uhsService,
        UHSModelAssembler assembler,
        PermissionVerifier permissionVerifier,
        UserRepository userRepository,
        MissionGroupRepository missionGroupRepository
    ) {
        this.uhsService = uhsService;
        this.assembler = assembler;
        this.permissionVerifier = permissionVerifier;
        this.userRepository = userRepository;
        this.missionGroupRepository = missionGroupRepository;
    }

    @GetMapping("")
    CollectionModel<EntityModel<UHSDto>> all(@AuthenticationPrincipal User userDetails) {
        if (
            !this.permissionVerifier.hasAccess(
                userDetails,
                SecurityGroup.UserRoleScopeEnum.VIEW,
                SecurityGroup.UserRoleTypeEnum.UHS
            )
        ) {
            throw new ForbiddenException("User does not have permission to view UHS.");
        }
        List<EntityModel<UHSDto>> entities = this.uhsService.findAll()
            .stream()
            .map(uhs -> this.uhsService.toDto(uhs, userDetails))
            .map(this.assembler::toModel)
            .collect(Collectors.toList());
        return CollectionModel.of(entities, linkTo(methodOn(UHSController.class).all(null)).withSelfRel());
    }

    @PostMapping("")
    EntityModel<UHSDto> newEntity(
        @RequestBody UHSCreatePayload newEntity,
        @AuthenticationPrincipal User userDetails
    ) {
        if (
            !this.permissionVerifier.hasAccess(
                userDetails,
                SecurityGroup.UserRoleScopeEnum.CREATE,
                SecurityGroup.UserRoleTypeEnum.UHS
            )
        ) {
            throw new ForbiddenException("User does not have permission to create UHS.");
        }

        UHS entity = new UHS();
        entity.setName(newEntity.name());
        entity.setLocation(EmbeddedPosition.of(newEntity.location()));
        entity.setCapacity(newEntity.capacity());
        entity.setAssignedPersonell(
            newEntity.assignedPersonellIds() != null
                ? new HashSet<>(userRepository.findAllById(newEntity.assignedPersonellIds()))
                : new HashSet<>()
        );
        entity.setMission(
            newEntity.missionId() != null ? missionGroupRepository.findById(newEntity.missionId()).orElse(null) : null
        );

        entity = this.uhsService.save(entity);
        return this.assembler.toModel(this.uhsService.toDto(entity, userDetails));
    }

    @GetMapping("{id}")
    EntityModel<UHSDto> one(@PathVariable UUID id, @AuthenticationPrincipal User userDetails) {
        UHS entity = this.uhsService.findById(id).orElseThrow(() -> new UHSNotFoundException(id));
        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.VIEW, entity)) {
            throw new ForbiddenException("User does not have permission to view UHS.");
        }
        return this.assembler.toModel(this.uhsService.toDto(entity, userDetails));
    }

    @PutMapping("{id}")
    EntityModel<UHSDto> replaceEntity(
        @RequestBody UHSCreatePayload newEntity,
        @PathVariable UUID id,
        @AuthenticationPrincipal User userDetails
    ) {
        UHS entity = this.uhsService.findById(id).orElseThrow(() -> new UHSNotFoundException(id));
        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.EDIT, entity)) {
            throw new ForbiddenException("User does not have permission to edit UHS.");
        }

        entity.setName(newEntity.name());
        entity.setLocation(EmbeddedPosition.of(newEntity.location()));
        entity.setCapacity(newEntity.capacity());
        entity.setAssignedPersonell(
            newEntity.assignedPersonellIds() != null
                ? new HashSet<>(userRepository.findAllById(newEntity.assignedPersonellIds()))
                : new HashSet<>()
        );
        entity.setMission(
            newEntity.missionId() != null ? missionGroupRepository.findById(newEntity.missionId()).orElse(null) : null
        );

        entity = this.uhsService.save(entity);
        return this.assembler.toModel(this.uhsService.toDto(entity, userDetails));
    }

    @DeleteMapping("{id}")
    void deleteEntity(@PathVariable UUID id, @AuthenticationPrincipal User userDetails) {
        UHS entity = this.uhsService.findById(id).orElseThrow(() -> new UHSNotFoundException(id));
        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.DELETE, entity)) {
            throw new ForbiddenException("User does not have permission to delete UHS.");
        }
        this.uhsService.deleteById(id);
    }

    public record UHSCreatePayload(
        String name,
        EmbeddedPositionDto location,
        Integer capacity,
        List<UUID> assignedPersonellIds,
        UUID missionId
    ) {}
}
