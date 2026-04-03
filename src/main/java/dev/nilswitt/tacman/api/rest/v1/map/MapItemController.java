package dev.nilswitt.tacman.api.rest.v1.map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import dev.nilswitt.tacman.api.dtos.EmbeddedPositionDto;
import dev.nilswitt.tacman.api.dtos.MapItemDto;
import dev.nilswitt.tacman.entities.EmbeddedPosition;
import dev.nilswitt.tacman.entities.MapItem;
import dev.nilswitt.tacman.entities.SecurityGroup;
import dev.nilswitt.tacman.entities.User;
import dev.nilswitt.tacman.exceptions.ForbiddenException;
import dev.nilswitt.tacman.exceptions.MapItemNotFoundException;
import dev.nilswitt.tacman.security.PermissionVerifier;
import dev.nilswitt.tacman.services.MapGroupService;
import dev.nilswitt.tacman.services.MapItemService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/map/items")
public class MapItemController {

    private final MapItemService mapItemService;
    private final MapItemModelAssembler assembler;
    private final PermissionVerifier permissionVerifier;
    private final MapGroupService mapGroupService;

    public MapItemController(
        MapItemService mapItemService,
        MapItemModelAssembler assembler,
        PermissionVerifier permissionVerifier,
        MapGroupService mapGroupService
    ) {
        this.mapItemService = mapItemService;
        this.permissionVerifier = permissionVerifier;
        this.assembler = assembler;
        this.mapGroupService = mapGroupService;
    }

    @GetMapping("")
    CollectionModel<EntityModel<MapItemDto>> all(@AuthenticationPrincipal User userDetails) {
        if (
            this.permissionVerifier.hasAccess(
                userDetails,
                SecurityGroup.UserRoleScopeEnum.VIEW,
                SecurityGroup.UserRoleTypeEnum.MAPITEM
            )
        ) {
            List<EntityModel<MapItemDto>> entities = this.mapItemService.findAll()
                .stream()
                .map(mapItem -> this.mapItemService.toDto(mapItem, userDetails))
                .map(this.assembler::toModel)
                .collect(Collectors.toList());
            return CollectionModel.of(entities, linkTo(methodOn(MapItemController.class).all(null)).withSelfRel());
        }

        return CollectionModel.of(
            this.permissionVerifier.getMapItemsForUser(userDetails)
                .stream()
                .map(mapItem -> this.mapItemService.toDto(mapItem, userDetails))
                .map(this.assembler::toModel)
                .collect(Collectors.toList()),
            linkTo(methodOn(MapItemController.class).all(null)).withSelfRel()
        );
    }

    @PostMapping("")
    EntityModel<MapItemDto> newEntity(
        @RequestBody MapItemCreatePayload newEntity,
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

        MapItem newMapItem = new MapItem();
        newMapItem.setName(newEntity.name());
        newMapItem.setPosition(EmbeddedPosition.of(newEntity.position()));
        newMapItem.setZoomLevel(newEntity.zoomLevel());
        newMapItem.setMapGroup(
            newEntity.mapGroupId() != null ? mapGroupService.findById(newEntity.mapGroupId()).orElse(null) : null
        );

        newMapItem = this.mapItemService.save(newMapItem);

        return this.assembler.toModel(this.mapItemService.toDto(newMapItem, userDetails));
    }

    @GetMapping("{id}")
    EntityModel<MapItemDto> one(@PathVariable UUID id, @AuthenticationPrincipal User userDetails) {
        MapItem entity = this.mapItemService.findById(id).orElseThrow(() -> new MapItemNotFoundException(id));
        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.VIEW, entity)) {
            throw new ForbiddenException("User does not have permission to view overlays.");
        }

        return this.assembler.toModel(this.mapItemService.toDto(entity, userDetails));
    }

    @PutMapping("{id}")
    EntityModel<MapItemDto> replaceEntity(
        @RequestBody MapItemCreatePayload newEntity,
        @PathVariable UUID id,
        @AuthenticationPrincipal User userDetails
    ) {
        MapItem entity = this.mapItemService.findById(id).orElseThrow(() -> new MapItemNotFoundException(id));

        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.EDIT, entity)) {
            throw new ForbiddenException("User does not have permission to edit overlays.");
        }

        entity.setName(newEntity.name());
        entity.setPosition(EmbeddedPosition.of(newEntity.position()));
        entity.setZoomLevel(newEntity.zoomLevel());
        entity.setMapGroup(
            newEntity.mapGroupId() != null ? mapGroupService.findById(newEntity.mapGroupId()).orElse(null) : null
        );

        entity = this.mapItemService.save(entity);

        return this.assembler.toModel(this.mapItemService.toDto(entity, userDetails));
    }

    @DeleteMapping("{id}")
    void deleteEntity(@PathVariable UUID id, @AuthenticationPrincipal User userDetails) {
        MapItem entity = this.mapItemService.findById(id).orElseThrow(() -> new MapItemNotFoundException(id));

        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.DELETE, entity)) {
            throw new ForbiddenException("User does not have permission to delete overlays.");
        }
        this.mapItemService.deleteById(id);
    }

    public record MapItemCreatePayload(String name, EmbeddedPositionDto position, Integer zoomLevel, UUID mapGroupId) {}
}
