package dev.nilswitt.tacman.api.rest.v1;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import dev.nilswitt.tacman.api.dtos.EmbeddedPositionDto;
import dev.nilswitt.tacman.api.dtos.MissionGroupDto;
import dev.nilswitt.tacman.entities.EmbeddedPosition;
import dev.nilswitt.tacman.entities.MissionGroup;
import dev.nilswitt.tacman.entities.SecurityGroup;
import dev.nilswitt.tacman.entities.User;
import dev.nilswitt.tacman.exceptions.ForbiddenException;
import dev.nilswitt.tacman.exceptions.MissionGroupNotFoundException;
import dev.nilswitt.tacman.security.PermissionVerifier;
import dev.nilswitt.tacman.services.MapGroupService;
import dev.nilswitt.tacman.services.MissionGroupService;
import dev.nilswitt.tacman.services.UnitService;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/missiongroups")
public class MissionGroupController {

    private final MissionGroupService missionGroupService;
    private final MissionGroupModelAssembler assembler;
    private final PermissionVerifier permissionVerifier;
    private final UnitService unitService;
    private final MapGroupService mapGroupService;

    public MissionGroupController(
        MissionGroupService missionGroupService,
        MissionGroupModelAssembler assembler,
        PermissionVerifier permissionVerifier,
        UnitService unitService,
        MapGroupService mapGroupService
    ) {
        this.missionGroupService = missionGroupService;
        this.assembler = assembler;
        this.permissionVerifier = permissionVerifier;
        this.unitService = unitService;
        this.mapGroupService = mapGroupService;
    }

    @GetMapping("")
    CollectionModel<EntityModel<MissionGroupDto>> all(@AuthenticationPrincipal User userDetails) {
        if (
            this.permissionVerifier.hasAccess(
                userDetails,
                SecurityGroup.UserRoleScopeEnum.VIEW,
                SecurityGroup.UserRoleTypeEnum.MAPOVERLAY
            )
        ) {
            List<EntityModel<MissionGroupDto>> entities = this.missionGroupService.findAll()
                .stream()
                .map(missionGroup -> this.missionGroupService.toDto(missionGroup, userDetails))
                .map(this.assembler::toModel)
                .collect(Collectors.toList());
            return CollectionModel.of(entities, linkTo(methodOn(MissionGroupController.class).all(null)).withSelfRel());
        }

        return CollectionModel.of(
            this.permissionVerifier.getMissionGroupsForUser(userDetails)
                .stream()
                .map(missionGroup -> this.missionGroupService.toDto(missionGroup, userDetails))
                .map(this.assembler::toModel)
                .collect(Collectors.toList()),
            linkTo(methodOn(MissionGroupController.class).all(null)).withSelfRel()
        );
    }

    @PostMapping("")
    EntityModel<MissionGroupDto> newEntity(
        @RequestBody MissionGroupCreatePayload newEntity,
        @AuthenticationPrincipal User userDetails
    ) {
        if (
            !this.permissionVerifier.hasAccess(
                userDetails,
                SecurityGroup.UserRoleScopeEnum.CREATE,
                SecurityGroup.UserRoleTypeEnum.MAPOVERLAY
            )
        ) {
            throw new ForbiddenException("User does not have permission to create overlays.");
        }

        MissionGroup entity = new MissionGroup();
        entity.setName(newEntity.name());
        entity.setStartTime(newEntity.startTime());
        entity.setEndTime(newEntity.endTime());
        entity.setPosition(EmbeddedPosition.of(newEntity.position()));
        entity.setUnits(new HashSet<>(unitService.findAllById(newEntity.unitIds())));
        entity.setMapGroups(new HashSet<>(mapGroupService.findAllById(newEntity.mapGroupIds())));

        entity = this.missionGroupService.save(entity);
        return this.assembler.toModel(this.missionGroupService.toDto(entity, userDetails));
    }

    @GetMapping("{id}")
    EntityModel<MissionGroupDto> one(@PathVariable UUID id, @AuthenticationPrincipal User userDetails) {
        MissionGroup entity = this.missionGroupService.findById(id).orElseThrow(() ->
            new MissionGroupNotFoundException(id)
        );
        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.VIEW, entity)) {
            throw new ForbiddenException("User does not have permission to view overlays.");
        }
        return this.assembler.toModel(this.missionGroupService.toDto(entity, userDetails));
    }

    @PutMapping("{id}")
    EntityModel<MissionGroupDto> replaceEntity(
        @RequestBody MissionGroupCreatePayload newEntity,
        @PathVariable UUID id,
        @AuthenticationPrincipal User userDetails
    ) {
        MissionGroup entity = this.missionGroupService.findById(id).orElseThrow(() ->
            new MissionGroupNotFoundException(id)
        );

        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.EDIT, entity)) {
            throw new ForbiddenException("User does not have permission to edit overlays.");
        }

        entity.setName(newEntity.name());
        entity.setStartTime(newEntity.startTime());
        entity.setEndTime(newEntity.endTime());
        entity.setPosition(EmbeddedPosition.of(newEntity.position()));
        entity.setUnits(new HashSet<>(unitService.findAllById(newEntity.unitIds())));
        entity.setMapGroups(new HashSet<>(mapGroupService.findAllById(newEntity.mapGroupIds())));

        MissionGroup saved = this.missionGroupService.save(entity);
        return this.assembler.toModel(this.missionGroupService.toDto(saved, userDetails));
    }

    @DeleteMapping("{id}")
    void deleteEntity(@PathVariable UUID id, @AuthenticationPrincipal User userDetails) {
        MissionGroup entity = this.missionGroupService.findById(id).orElseThrow(() ->
            new MissionGroupNotFoundException(id)
        );

        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.DELETE, entity)) {
            throw new ForbiddenException("User does not have permission to delete overlays.");
        }
        this.missionGroupService.deleteById(id);
    }

    public record MissionGroupCreatePayload(
        String name,
        Instant startTime,
        Instant endTime,
        List<UUID> unitIds,
        List<UUID> mapGroupIds,
        EmbeddedPositionDto position
    ) {}
}
