package dev.nilswitt.tacman.api.rest.v1.map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import dev.nilswitt.tacman.api.dtos.MapBaseLayerDto;
import dev.nilswitt.tacman.entities.MapBaseLayer;
import dev.nilswitt.tacman.entities.SecurityGroup;
import dev.nilswitt.tacman.entities.User;
import dev.nilswitt.tacman.exceptions.ForbiddenException;
import dev.nilswitt.tacman.exceptions.MapBaseLayerNotFoundException;
import dev.nilswitt.tacman.security.PermissionVerifier;
import dev.nilswitt.tacman.services.MapBaseLayerService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("api/map/baselayers")
public class MapBaseLayerController {

    private final MapBaseLayerService mapBaseLayerService;
    private final MapBaseLayerModelAssembler assembler;
    private final PermissionVerifier permissionVerifier;

    public MapBaseLayerController(
        MapBaseLayerService mapBaseLayerService,
        MapBaseLayerModelAssembler assembler,
        PermissionVerifier permissionVerifier
    ) {
        this.mapBaseLayerService = mapBaseLayerService;
        this.assembler = assembler;
        this.permissionVerifier = permissionVerifier;
    }

    @GetMapping("")
    CollectionModel<EntityModel<MapBaseLayerDto>> all(@AuthenticationPrincipal User userDetails) {
        if (
            this.permissionVerifier.hasAccess(
                userDetails,
                SecurityGroup.UserRoleScopeEnum.VIEW,
                SecurityGroup.UserRoleTypeEnum.MAPOVERLAY
            )
        ) {
            List<EntityModel<MapBaseLayerDto>> entities = this.mapBaseLayerService.findAll()
                .stream()
                .map(mapBaseLayer -> this.mapBaseLayerService.toDto(mapBaseLayer, userDetails))
                .map(this.assembler::toModel)
                .collect(Collectors.toList());
            return CollectionModel.of(entities, linkTo(methodOn(MapBaseLayerController.class).all(null)).withSelfRel());
        }
        log.info(
            "User {} does not have permission to view all baselayers, returning only those with permissions.",
            userDetails.getUsername()
        );
        return CollectionModel.of(
            this.permissionVerifier.getMapBaseLayersForUser(userDetails)
                .stream()
                .map(mapBaseLayer -> this.mapBaseLayerService.toDto(mapBaseLayer, userDetails))
                .map(this.assembler::toModel)
                .collect(Collectors.toList()),
            linkTo(methodOn(MapBaseLayerController.class).all(null)).withSelfRel()
        );
    }

    @PostMapping("")
    EntityModel<MapBaseLayerDto> newEntity(
        @RequestBody MapBaseLayerCreatePayload newEntity,
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
        MapBaseLayer newBaseLayer = new MapBaseLayer();
        newBaseLayer.setName(newEntity.name());
        newBaseLayer.setCacheUrl(newEntity.cacheUrl());
        newBaseLayer.setUrl(newEntity.url());
        newBaseLayer = this.mapBaseLayerService.save(newBaseLayer);

        return this.assembler.toModel(this.mapBaseLayerService.toDto(newBaseLayer, userDetails));
    }

    @GetMapping("{id}")
    EntityModel<MapBaseLayerDto> one(@PathVariable UUID id, @AuthenticationPrincipal User userDetails) {
        MapBaseLayer entity = this.mapBaseLayerService.findById(id).orElseThrow(() ->
            new MapBaseLayerNotFoundException(id)
        );
        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.VIEW, entity)) {
            throw new ForbiddenException("User does not have permission to view overlays.");
        }

        return this.assembler.toModel(this.mapBaseLayerService.toDto(entity, userDetails));
    }

    @PutMapping("{id}")
    EntityModel<MapBaseLayerDto> replaceEntity(
        @RequestBody MapBaseLayerCreatePayload newEntity,
        @PathVariable UUID id,
        @AuthenticationPrincipal User userDetails
    ) {
        MapBaseLayer entity = this.mapBaseLayerService.findById(id).orElseThrow(() ->
            new MapBaseLayerNotFoundException(id)
        );

        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.EDIT, entity)) {
            throw new ForbiddenException("User does not have permission to edit overlays.");
        }

        entity.setName(newEntity.name());
        entity.setCacheUrl(newEntity.cacheUrl());
        entity.setUrl(newEntity.url());

        entity = this.mapBaseLayerService.save(entity);
        return this.assembler.toModel(this.mapBaseLayerService.toDto(entity, userDetails));
    }

    @DeleteMapping("{id}")
    void deleteEntity(@PathVariable UUID id, @AuthenticationPrincipal User userDetails) {
        MapBaseLayer entity = this.mapBaseLayerService.findById(id).orElseThrow(() ->
            new MapBaseLayerNotFoundException(id)
        );

        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.DELETE, entity)) {
            throw new ForbiddenException("User does not have permission to delete overlays.");
        }
        this.mapBaseLayerService.deleteById(id);
    }

    public record MapBaseLayerCreatePayload(String name, String url, String cacheUrl) {}
}
