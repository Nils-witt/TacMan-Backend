package dev.nilswitt.tacman.api.rest.v1.map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import dev.nilswitt.tacman.api.dtos.MapGroupDto;
import dev.nilswitt.tacman.entities.MapGroup;
import dev.nilswitt.tacman.entities.SecurityGroup;
import dev.nilswitt.tacman.entities.User;
import dev.nilswitt.tacman.entities.repositories.MapGroupRepository;
import dev.nilswitt.tacman.exceptions.ForbiddenException;
import dev.nilswitt.tacman.exceptions.MapItemNotFoundException;
import dev.nilswitt.tacman.security.PermissionVerifier;
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

    private final MapGroupRepository repository;
    private final MapGroupModelAssembler assembler;
    private final PermissionVerifier permissionVerifier;

    public MapGroupController(
        MapGroupRepository repository,
        MapGroupModelAssembler assembler,
        PermissionVerifier permissionVerifier
    ) {
        this.repository = repository;
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
            List<EntityModel<MapGroupDto>> entities = this.repository.findAll()
                .stream()
                .map(mapGroup -> {
                    MapGroupDto dto = mapGroup.toDto();
                    dto.setPermissions(this.permissionVerifier.getScopes(mapGroup, userDetails));
                    return dto;
                })
                .map(this.assembler::toModel)
                .collect(Collectors.toList());
            return CollectionModel.of(entities, linkTo(methodOn(MapGroupController.class).all(null)).withSelfRel());
        }

        return CollectionModel.of(
            this.permissionVerifier.getMapGroupsForUser(userDetails)
                .stream()
                .map(mapGroup -> {
                    MapGroupDto dto = mapGroup.toDto();
                    dto.setPermissions(this.permissionVerifier.getScopes(mapGroup, userDetails));
                    return dto;
                })
                .map(this.assembler::toModel)
                .collect(Collectors.toList()),
            linkTo(methodOn(MapGroupController.class).all(null)).withSelfRel()
        );
    }

    @PostMapping("")
    EntityModel<MapGroupDto> newEntity(@RequestBody MapGroupDto newEntity, @AuthenticationPrincipal User userDetails) {
        if (
            !this.permissionVerifier.hasAccess(
                userDetails,
                SecurityGroup.UserRoleScopeEnum.CREATE,
                SecurityGroup.UserRoleTypeEnum.MAPITEM
            )
        ) {
            throw new ForbiddenException("User does not have permission to create overlays.");
        }

        MapGroup mapItem = new MapGroup();
        mapItem.setName(newEntity.getName());

        MapGroup saved = this.repository.save(mapItem);
        MapGroupDto dto = saved.toDto();
        dto.setPermissions(this.permissionVerifier.getScopes(saved, userDetails));
        return this.assembler.toModel(dto);
    }

    @GetMapping("{id}")
    EntityModel<MapGroupDto> one(@PathVariable UUID id, @AuthenticationPrincipal User userDetails) {
        MapGroup entity = this.repository.findById(id).orElseThrow(() -> new MapItemNotFoundException(id));
        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.VIEW, entity)) {
            throw new ForbiddenException("User does not have permission to view overlays.");
        }

        MapGroupDto dto = entity.toDto();
        dto.setPermissions(this.permissionVerifier.getScopes(entity, userDetails));
        return this.assembler.toModel(dto);
    }

    @PutMapping("{id}")
    EntityModel<MapGroupDto> replaceEntity(
        @RequestBody MapGroupDto newEntity,
        @PathVariable UUID id,
        @AuthenticationPrincipal User userDetails
    ) {
        MapGroup entity = this.repository.findById(id).orElseThrow(() -> new MapItemNotFoundException(id));

        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.EDIT, entity)) {
            throw new ForbiddenException("User does not have permission to edit overlays.");
        }

        entity.setName(newEntity.getName());

        MapGroup saved = this.repository.save(entity);
        MapGroupDto dto = saved.toDto();
        dto.setPermissions(this.permissionVerifier.getScopes(saved, userDetails));
        return this.assembler.toModel(dto);
    }

    @DeleteMapping("{id}")
    void deleteEntity(@PathVariable UUID id, @AuthenticationPrincipal User userDetails) {
        MapGroup entity = this.repository.findById(id).orElseThrow(() -> new MapItemNotFoundException(id));

        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.DELETE, entity)) {
            throw new ForbiddenException("User does not have permission to delete overlays.");
        }
        this.repository.deleteById(id);
    }
}
