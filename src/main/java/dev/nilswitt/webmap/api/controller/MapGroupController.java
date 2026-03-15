package dev.nilswitt.webmap.api.controller;

import dev.nilswitt.webmap.api.dtos.MapGroupDto;
import dev.nilswitt.webmap.api.exceptions.ForbiddenException;
import dev.nilswitt.webmap.api.exceptions.MapItemNotFoundException;
import dev.nilswitt.webmap.entities.MapGroup;
import dev.nilswitt.webmap.entities.SecurityGroup;
import dev.nilswitt.webmap.entities.User;
import dev.nilswitt.webmap.entities.repositories.MapGroupRepository;
import dev.nilswitt.webmap.security.PermissionUtil;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("api/map/groups")
public class MapGroupController {

    private final MapGroupRepository repository;
    private final MapGroupModelAssembler assembler;
    private final PermissionUtil permissionUtil;

    public MapGroupController(MapGroupRepository repository, MapGroupModelAssembler assembler, PermissionUtil permissionUtil) {
        this.repository = repository;
        this.permissionUtil = permissionUtil;
        this.assembler = assembler;
    }

    @GetMapping("")
    CollectionModel<EntityModel<MapGroupDto>> all(@AuthenticationPrincipal User userDetails) {
        if (this.permissionUtil.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.VIEW, SecurityGroup.UserRoleTypeEnum.MAPITEM)) {

            List<EntityModel<MapGroupDto>> entities = this.repository.findAll().stream()
                    .map(mapGroup -> {
                        MapGroupDto dto = mapGroup.toDto();
                        dto.setPermissions(this.permissionUtil.getScopes(mapGroup, userDetails));
                        return dto;
                    })
                    .map(this.assembler::toModel)
                    .collect(Collectors.toList());
            return CollectionModel.of(entities, linkTo(methodOn(MapGroupController.class).all(null)).withSelfRel());
        }

        return CollectionModel.of(this.permissionUtil.getMapGroupsForUser(userDetails).stream().map(mapGroup -> {
            MapGroupDto dto = mapGroup.toDto();
            dto.setPermissions(this.permissionUtil.getScopes(mapGroup, userDetails));
            return dto;
        }).map(this.assembler::toModel).collect(Collectors.toList()), linkTo(methodOn(MapGroupController.class).all(null)).withSelfRel());

    }

    @PostMapping("")
    EntityModel<MapGroupDto> newEntity(@RequestBody MapGroupDto newEntity, @AuthenticationPrincipal User userDetails) {
        if (!this.permissionUtil.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.CREATE, SecurityGroup.UserRoleTypeEnum.MAPITEM)) {
            throw new ForbiddenException("User does not have permission to create overlays.");
        }

        MapGroup mapItem = new MapGroup();
        mapItem.setName(newEntity.getName());

        MapGroup saved = this.repository.save(mapItem);
        MapGroupDto dto = saved.toDto();
        dto.setPermissions(this.permissionUtil.getScopes(saved, userDetails));
        return this.assembler.toModel(dto);
    }

    @GetMapping("{id}")
    EntityModel<MapGroupDto> one(@PathVariable UUID id, @AuthenticationPrincipal User userDetails) {
        MapGroup entity = this.repository.findById(id).orElseThrow(() -> new MapItemNotFoundException(id));
        if (!this.permissionUtil.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.VIEW, entity)) {
            throw new ForbiddenException("User does not have permission to view overlays.");
        }

        MapGroupDto dto = entity.toDto();
        dto.setPermissions(this.permissionUtil.getScopes(entity, userDetails));
        return this.assembler.toModel(dto);
    }

    @PutMapping("{id}")
    EntityModel<MapGroupDto> replaceEntity(@RequestBody MapGroupDto newEntity, @PathVariable UUID id, @AuthenticationPrincipal User userDetails) {
        MapGroup entity = this.repository.findById(id).orElseThrow(() -> new MapItemNotFoundException(id));

        if (!this.permissionUtil.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.EDIT, entity)) {
            throw new ForbiddenException("User does not have permission to edit overlays.");
        }

        entity.setName(newEntity.getName());

        MapGroup saved = this.repository.save(entity);
        MapGroupDto dto = saved.toDto();
        dto.setPermissions(this.permissionUtil.getScopes(saved, userDetails));
        return this.assembler.toModel(dto);
    }

    @DeleteMapping("{id}")
    void deleteEntity(@PathVariable UUID id, @AuthenticationPrincipal User userDetails) {
        MapGroup entity = this.repository.findById(id).orElseThrow(() -> new MapItemNotFoundException(id));

        if (!this.permissionUtil.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.DELETE, entity)) {
            throw new ForbiddenException("User does not have permission to delete overlays.");
        }
        this.repository.deleteById(id);
    }
}
