package dev.nilswitt.webmap.api.controller;

import dev.nilswitt.webmap.api.dtos.MissionGroupDto;
import dev.nilswitt.webmap.api.exceptions.ForbiddenException;
import dev.nilswitt.webmap.api.exceptions.MissionGroupNotFoundException;
import dev.nilswitt.webmap.entities.MissionGroup;
import dev.nilswitt.webmap.entities.SecurityGroup;
import dev.nilswitt.webmap.entities.User;
import dev.nilswitt.webmap.entities.repositories.MissionGroupRepository;
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
@RequestMapping("api/missiongroups")
public class MissionGroupController {

    private final MissionGroupRepository repository;
    private final MissionGroupModelAssembler assembler;
    private final PermissionUtil permissionUtil;

    public MissionGroupController(MissionGroupRepository repository, MissionGroupModelAssembler assembler, PermissionUtil permissionUtil) {
        this.repository = repository;
        this.assembler = assembler;
        this.permissionUtil = permissionUtil;
    }

    @GetMapping("")
    CollectionModel<EntityModel<MissionGroupDto>> all(@AuthenticationPrincipal User userDetails) {

        if (this.permissionUtil.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.VIEW, SecurityGroup.UserRoleTypeEnum.MAPOVERLAY)) {

            List<EntityModel<MissionGroupDto>> entities = this.repository.findAll().stream()
                    .map(missionGroup -> {
                        MissionGroupDto dto = missionGroup.toDto();
                        dto.setPermissions(this.permissionUtil.getScopes(missionGroup, userDetails));
                        return dto;
                    })
                    .map(this.assembler::toModel)
                    .collect(Collectors.toList());
            return CollectionModel.of(entities, linkTo(methodOn(MissionGroupController.class).all(null)).withSelfRel());
        }

        return CollectionModel.of(this.permissionUtil.getMissionGroupsForUser(userDetails).stream().map(missionGroup -> {
            MissionGroupDto dto = missionGroup.toDto();
            dto.setPermissions(this.permissionUtil.getScopes(missionGroup, userDetails));
            return dto;
        }).map(this.assembler::toModel).collect(Collectors.toList()), linkTo(methodOn(MissionGroupController.class).all(null)).withSelfRel());
    }


    @PostMapping("")
    EntityModel<MissionGroupDto> newEntity(@RequestBody MissionGroup newEntity, @AuthenticationPrincipal User userDetails) {
        if (!this.permissionUtil.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.CREATE, SecurityGroup.UserRoleTypeEnum.MAPOVERLAY)) {
            throw new ForbiddenException("User does not have permission to create overlays.");
        }
        MissionGroup entity = this.repository.save(newEntity);
        MissionGroupDto dto = entity.toDto();
        dto.setPermissions(this.permissionUtil.getScopes(entity, userDetails));
        return this.assembler.toModel(dto);
    }

    @GetMapping("{id}")
    EntityModel<MissionGroupDto> one(@PathVariable UUID id, @AuthenticationPrincipal User userDetails) {
        MissionGroup entity = this.repository.findById(id).orElseThrow(() -> new MissionGroupNotFoundException(id));
        if (!this.permissionUtil.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.VIEW, entity)) {
            throw new ForbiddenException("User does not have permission to view overlays.");
        }
        MissionGroupDto dto = entity.toDto();
        dto.setPermissions(this.permissionUtil.getScopes(entity, userDetails));
        return this.assembler.toModel(dto);
    }

    @PutMapping("{id}")
    EntityModel<MissionGroupDto> replaceEntity(@RequestBody MissionGroup newEntity, @PathVariable UUID id, @AuthenticationPrincipal User userDetails) {
        MissionGroup entity = this.repository.findById(id).orElseThrow(() -> new MissionGroupNotFoundException(id));

        if (!this.permissionUtil.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.EDIT, entity)) {
            throw new ForbiddenException("User does not have permission to edit overlays.");
        }

        entity.setName(newEntity.getName());
        entity.setStartTime(newEntity.getStartTime());

        MissionGroup saved = this.repository.save(entity);
        MissionGroupDto dto = saved.toDto();
        dto.setPermissions(this.permissionUtil.getScopes(saved, userDetails));
        return this.assembler.toModel(dto);
    }

    @DeleteMapping("{id}")
    void deleteEntity(@PathVariable UUID id, @AuthenticationPrincipal User userDetails) {
        MissionGroup entity = this.repository.findById(id).orElseThrow(() -> new MissionGroupNotFoundException(id));

        if (!this.permissionUtil.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.DELETE, entity)) {
            throw new ForbiddenException("User does not have permission to delete overlays.");
        }
        this.repository.deleteById(id);
    }
}
