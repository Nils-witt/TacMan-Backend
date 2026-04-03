package dev.nilswitt.tacman.api.rest.v1.map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import dev.nilswitt.tacman.api.dtos.MapGroupDto;
import dev.nilswitt.tacman.entities.MapGroup;
import dev.nilswitt.tacman.entities.SecurityGroup;
import dev.nilswitt.tacman.entities.User;
import dev.nilswitt.tacman.exceptions.ForbiddenException;
import dev.nilswitt.tacman.exceptions.MapItemNotFoundException;
import dev.nilswitt.tacman.security.PermissionVerifier;
import dev.nilswitt.tacman.services.MapGroupService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/map/groups")
public class MapGroupController {

    private final MapGroupService mapGroupService;
    private final MapGroupModelAssembler assembler;
    private final PermissionVerifier permissionVerifier;

    public MapGroupController(
        MapGroupService mapGroupService,
        MapGroupModelAssembler assembler,
        PermissionVerifier permissionVerifier
    ) {
        this.mapGroupService = mapGroupService;
        this.permissionVerifier = permissionVerifier;
        this.assembler = assembler;
    }

    @GetMapping("")
    CollectionModel<EntityModel<MapGroupDto>> all(@AuthenticationPrincipal User userDetails) {
        if (
            this.permissionVerifier.hasAccess(
                userDetails,
                SecurityGroup.UserRoleScopeEnum.VIEW,
                SecurityGroup.UserRoleTypeEnum.MAPITEM
            )
        ) {
            List<EntityModel<MapGroupDto>> entities = this.mapGroupService.findAll()
                .stream()
                .map(mapGroup -> this.mapGroupService.toDto(mapGroup, userDetails))
                .map(this.assembler::toModel)
                .collect(Collectors.toList());
            return CollectionModel.of(entities, linkTo(methodOn(MapGroupController.class).all(null)).withSelfRel());
        }

        return CollectionModel.of(
            this.permissionVerifier.getMapGroupsForUser(userDetails)
                .stream()
                .map(mapGroup -> this.mapGroupService.toDto(mapGroup, userDetails))
                .map(this.assembler::toModel)
                .collect(Collectors.toList()),
            linkTo(methodOn(MapGroupController.class).all(null)).withSelfRel()
        );
    }

    @PostMapping("")
    EntityModel<MapGroupDto> newEntity(
        @RequestBody MapGroupCreatePayload newEntity,
        @AuthenticationPrincipal User userDetails
    ) {
        if (
            !this.permissionVerifier.hasAccess(
                userDetails,
                SecurityGroup.UserRoleScopeEnum.CREATE,
                SecurityGroup.UserRoleTypeEnum.MAPITEM
            )
        ) {
            throw new ForbiddenException("User does not have permission to create overlays.");
        }
        MapGroup mapGroup = new MapGroup();
        mapGroup.setName(newEntity.name());

        mapGroup = this.mapGroupService.save(mapGroup);

        return this.assembler.toModel(this.mapGroupService.toDto(mapGroup, userDetails));
    }

    @GetMapping("{id}")
    EntityModel<MapGroupDto> one(@PathVariable UUID id, @AuthenticationPrincipal User userDetails) {
        MapGroup entity = this.mapGroupService.findById(id).orElseThrow(() -> new MapItemNotFoundException(id));
        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.VIEW, entity)) {
            throw new ForbiddenException("User does not have permission to view overlays.");
        }

        return this.assembler.toModel(this.mapGroupService.toDto(entity, userDetails));
    }

    @PutMapping("{id}")
    EntityModel<MapGroupDto> replaceEntity(
        @RequestBody MapGroupCreatePayload newEntity,
        @PathVariable UUID id,
        @AuthenticationPrincipal User userDetails
    ) {
        MapGroup entity = this.mapGroupService.findById(id).orElseThrow(() -> new MapItemNotFoundException(id));

        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.EDIT, entity)) {
            throw new ForbiddenException("User does not have permission to edit overlays.");
        }

        entity.setName(newEntity.name());

        entity = this.mapGroupService.save(entity);

        return this.assembler.toModel(this.mapGroupService.toDto(entity, userDetails));
    }

    @DeleteMapping("{id}")
    void deleteEntity(@PathVariable UUID id, @AuthenticationPrincipal User userDetails) {
        MapGroup entity = this.mapGroupService.findById(id).orElseThrow(() -> new MapItemNotFoundException(id));

        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.DELETE, entity)) {
            throw new ForbiddenException("User does not have permission to delete overlays.");
        }
        this.mapGroupService.deleteById(id);
    }

    public record MapGroupCreatePayload(String name) {}
}
