package dev.nilswitt.tacman.api;

import dev.nilswitt.tacman.api.dtos.EntityPermissionsDto;
import dev.nilswitt.tacman.api.dtos.GroupPermissionEntryDto;
import dev.nilswitt.tacman.api.dtos.UserPermissionEntryDto;
import dev.nilswitt.tacman.entities.*;
import dev.nilswitt.tacman.entities.repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PermissionManagementService {

    private final UserPermissionsRepository userPermRepo;
    private final SecurityGroupPermissionsRepository groupPermRepo;
    private final UserRepository userRepo;
    private final SecurityGroupRepository groupRepo;
    private final UnitRepository unitRepo;
    private final MapOverlayRepository mapOverlayRepo;
    private final MapBaseLayerRepository mapBaseLayerRepo;
    private final MapItemRepository mapItemRepo;
    private final MapGroupRepository mapGroupRepo;
    private final MissionGroupRepository missionGroupRepo;
    private final PhotoRepository photoRepo;

    public PermissionManagementService(
            UserPermissionsRepository userPermRepo,
            SecurityGroupPermissionsRepository groupPermRepo,
            UserRepository userRepo,
            SecurityGroupRepository groupRepo,
            UnitRepository unitRepo,
            MapOverlayRepository mapOverlayRepo,
            MapBaseLayerRepository mapBaseLayerRepo,
            MapItemRepository mapItemRepo,
            MapGroupRepository mapGroupRepo,
            MissionGroupRepository missionGroupRepo,
            PhotoRepository photoRepo
    ) {
        this.userPermRepo = userPermRepo;
        this.groupPermRepo = groupPermRepo;
        this.userRepo = userRepo;
        this.groupRepo = groupRepo;
        this.unitRepo = unitRepo;
        this.mapOverlayRepo = mapOverlayRepo;
        this.mapBaseLayerRepo = mapBaseLayerRepo;
        this.mapItemRepo = mapItemRepo;
        this.mapGroupRepo = mapGroupRepo;
        this.missionGroupRepo = missionGroupRepo;
        this.photoRepo = photoRepo;
    }

    public AbstractEntity findEntity(String entityType, UUID entityId) {
        return switch (entityType.toLowerCase()) {
            case "unit" -> unitRepo
                    .findById(entityId)
                    .orElseThrow(() -> notFound("Unit", entityId));
            case "user" -> userRepo
                    .findById(entityId)
                    .orElseThrow(() -> notFound("User", entityId));
            case "mapoverlay" -> mapOverlayRepo
                    .findById(entityId)
                    .orElseThrow(() -> notFound("MapOverlay", entityId));
            case "mapbaselayer" -> mapBaseLayerRepo
                    .findById(entityId)
                    .orElseThrow(() -> notFound("MapBaseLayer", entityId));
            case "mapitem" -> mapItemRepo
                    .findById(entityId)
                    .orElseThrow(() -> notFound("MapItem", entityId));
            case "mapgroup" -> mapGroupRepo
                    .findById(entityId)
                    .orElseThrow(() -> notFound("MapGroup", entityId));
            case "missiongroup" -> missionGroupRepo
                    .findById(entityId)
                    .orElseThrow(() -> notFound("MissionGroup", entityId));
            case "photo" -> photoRepo
                    .findById(entityId)
                    .orElseThrow(() -> notFound("Photo", entityId));
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unknown entity type: " + entityType
            );
        };
    }

    public SecurityGroup.UserRoleTypeEnum resolveRoleType(String entityType) {
        return switch (entityType.toLowerCase()) {
            case "unit" -> SecurityGroup.UserRoleTypeEnum.UNIT;
            case "user" -> SecurityGroup.UserRoleTypeEnum.USER;
            case "mapoverlay" -> SecurityGroup.UserRoleTypeEnum.MAPOVERLAY;
            case "mapbaselayer" -> SecurityGroup.UserRoleTypeEnum.MAPBASELAYER;
            case "mapitem" -> SecurityGroup.UserRoleTypeEnum.MAPITEM;
            case "mapgroup" -> SecurityGroup.UserRoleTypeEnum.MAPGROUP;
            case "missiongroup" -> SecurityGroup.UserRoleTypeEnum.MISSIONGROUP;
            case "photo" -> SecurityGroup.UserRoleTypeEnum.PHOTO;
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unknown entity type: " + entityType
            );
        };
    }

    public EntityPermissionsDto getPermissions(AbstractEntity entity) {
        List<UserPermissionEntryDto> users = getUserPermissionsForEntity(entity)
                .stream()
                .map(p ->
                        new UserPermissionEntryDto(
                                p.getId(),
                                p.getUser().getId(),
                                p.getUser().getUsername(),
                                p.getScope()
                        )
                )
                .toList();

        List<GroupPermissionEntryDto> groups = getGroupPermissionsForEntity(entity)
                .stream()
                .map(p ->
                        new GroupPermissionEntryDto(
                                p.getId(),
                                p.getSecurityGroup().getId(),
                                p.getSecurityGroup().getName(),
                                p.getScope()
                        )
                )
                .toList();

        return new EntityPermissionsDto(users, groups);
    }

    public void grantUserPermission(
            AbstractEntity entity,
            UUID userId,
            SecurityGroup.UserRoleScopeEnum scope
    ) {
        User user = userRepo
                .findById(userId)
                .orElseThrow(() -> notFound("User", userId));
        UserPermission perm = findUserPermissionForEntity(user, entity).orElseGet(
                UserPermission::new
        );
        perm.setUser(user);
        perm.setScope(scope);
        setEntityOnUserPermission(perm, entity);
        userPermRepo.save(perm);
    }

    public void revokeUserPermission(AbstractEntity entity, UUID userId) {
        User user = userRepo
                .findById(userId)
                .orElseThrow(() -> notFound("User", userId));
        findUserPermissionForEntity(user, entity).ifPresent(userPermRepo::delete);
    }

    public void grantGroupPermission(
            AbstractEntity entity,
            UUID groupId,
            SecurityGroup.UserRoleScopeEnum scope
    ) {
        SecurityGroup group = groupRepo
                .findById(groupId)
                .orElseThrow(() -> notFound("SecurityGroup", groupId));
        SecurityGroupPermission perm = findGroupPermissionForEntity(
                group,
                entity
        ).orElseGet(SecurityGroupPermission::new);
        perm.setSecurityGroup(group);
        perm.setScope(scope);
        setEntityOnGroupPermission(perm, entity);
        groupPermRepo.save(perm);
    }

    public void revokeGroupPermission(AbstractEntity entity, UUID groupId) {
        SecurityGroup group = groupRepo
                .findById(groupId)
                .orElseThrow(() -> notFound("SecurityGroup", groupId));
        findGroupPermissionForEntity(group, entity).ifPresent(
                groupPermRepo::delete
        );
    }

    private List<UserPermission> getUserPermissionsForEntity(
            AbstractEntity entity
    ) {
        return switch (entity) {
            case Unit u -> userPermRepo.findByUnit(u);
            case MapOverlay o -> userPermRepo.findByMapOverlay(o);
            case MapBaseLayer bl -> userPermRepo.findByBaseLayer(bl);
            case MapItem mi -> userPermRepo.findByMapItem(mi);
            case MapGroup mg -> userPermRepo.findByMapGroup(mg);
            case MissionGroup mg -> userPermRepo.findByMissionGroup(mg);
            case User u -> userPermRepo.findByEntityUser(u);
            case Photo p -> userPermRepo.findByPhoto(p);
            default -> List.of();
        };
    }

    private List<SecurityGroupPermission> getGroupPermissionsForEntity(
            AbstractEntity entity
    ) {
        return switch (entity) {
            case Unit u -> groupPermRepo.findByUnit(u);
            case MapOverlay o -> groupPermRepo.findByMapOverlay(o);
            case MapBaseLayer bl -> groupPermRepo.findByBaseLayer(bl);
            case MapItem mi -> groupPermRepo.findByMapItem(mi);
            case MapGroup mg -> groupPermRepo.findByMapGroup(mg);
            case MissionGroup mg -> groupPermRepo.findByMissionGroup(mg);
            case User u -> groupPermRepo.findByEntityUser(u);
            case Photo p -> groupPermRepo.findByPhoto(p);
            default -> List.of();
        };
    }

    private Optional<UserPermission> findUserPermissionForEntity(
            User user,
            AbstractEntity entity
    ) {
        return switch (entity) {
            case Unit u -> userPermRepo.findByUserAndUnit(user, u);
            case MapOverlay o -> userPermRepo.findByUserAndMapOverlay(user, o);
            case MapBaseLayer bl -> userPermRepo.findByUserAndBaseLayer(user, bl);
            case MapItem mi -> userPermRepo.findByUserAndMapItem(user, mi);
            case MapGroup mg -> userPermRepo.findByUserAndMapGroup(user, mg);
            case MissionGroup mg -> userPermRepo.findByUserAndMissionGroup(user, mg);
            case User u -> userPermRepo.findByUserAndEntityUser(user, u);
            case Photo p -> userPermRepo.findByUserAndPhoto(user, p);
            default -> Optional.empty();
        };
    }

    private Optional<SecurityGroupPermission> findGroupPermissionForEntity(
            SecurityGroup group,
            AbstractEntity entity
    ) {
        return switch (entity) {
            case Unit u -> groupPermRepo.findBySecurityGroupAndUnit(group, u);
            case MapOverlay o -> groupPermRepo.findBySecurityGroupAndMapOverlay(
                    group,
                    o
            );
            case MapBaseLayer bl -> groupPermRepo.findBySecurityGroupAndBaseLayer(
                    group,
                    bl
            );
            case MapItem mi -> groupPermRepo.findBySecurityGroupAndMapItem(group, mi);
            case MapGroup mg -> groupPermRepo.findBySecurityGroupAndMapGroup(
                    group,
                    mg
            );
            case MissionGroup mg -> groupPermRepo.findBySecurityGroupAndMissionGroup(
                    group,
                    mg
            );
            case User u -> groupPermRepo.findBySecurityGroupAndEntityUser(group, u);
            case Photo p -> groupPermRepo.findBySecurityGroupAndPhoto(group, p);
            default -> Optional.empty();
        };
    }

    private void setEntityOnUserPermission(
            UserPermission perm,
            AbstractEntity entity
    ) {
        switch (entity) {
            case Unit u -> perm.setUnit(u);
            case MapOverlay o -> perm.setMapOverlay(o);
            case MapBaseLayer bl -> perm.setBaseLayer(bl);
            case MapItem mi -> perm.setMapItem(mi);
            case MapGroup mg -> perm.setMapGroup(mg);
            case MissionGroup mg -> perm.setMissionGroup(mg);
            case User u -> perm.setEntityUser(u);
            case Photo p -> perm.setPhoto(p);
            default -> {
            }
        }
    }

    private void setEntityOnGroupPermission(
            SecurityGroupPermission perm,
            AbstractEntity entity
    ) {
        switch (entity) {
            case Unit u -> perm.setUnit(u);
            case MapOverlay o -> perm.setMapOverlay(o);
            case MapBaseLayer bl -> perm.setBaseLayer(bl);
            case MapItem mi -> perm.setMapItem(mi);
            case MapGroup mg -> perm.setMapGroup(mg);
            case MissionGroup mg -> perm.setMissionGroup(mg);
            case User u -> perm.setEntityUser(u);
            case Photo p -> perm.setPhoto(p);
            default -> {
            }
        }
    }

    private ResponseStatusException notFound(String entityName, UUID id) {
        return new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                entityName + " not found: " + id
        );
    }
}
