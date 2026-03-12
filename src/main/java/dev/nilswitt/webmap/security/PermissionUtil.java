package dev.nilswitt.webmap.security;

import dev.nilswitt.webmap.entities.*;
import dev.nilswitt.webmap.entities.repositories.MapOverlayRepository;
import dev.nilswitt.webmap.entities.repositories.PhotoRepository;
import dev.nilswitt.webmap.entities.repositories.SecurityGroupPermissionsRepository;
import dev.nilswitt.webmap.entities.repositories.UserPermissionsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public final class PermissionUtil {

    private final UserPermissionsRepository userPermissionsRepository;
    private final SecurityGroupPermissionsRepository securityGroupPermissionsRepository;
    private final MapOverlayRepository mapOverlayRepository;
    private final PhotoRepository photoRepository;

    private PermissionUtil(
            UserPermissionsRepository userPermissionsRepository,
            SecurityGroupPermissionsRepository securityGroupPermissionsRepository,
            MapOverlayRepository mapOverlayRepository,
            PhotoRepository photoRepository
    ) {
        this.userPermissionsRepository = userPermissionsRepository;
        this.securityGroupPermissionsRepository = securityGroupPermissionsRepository;
        this.mapOverlayRepository = mapOverlayRepository;
        this.photoRepository = photoRepository;
    }

    public boolean hasAccess(User user, SecurityGroup.UserRoleScopeEnum requiredScope, SecurityGroup.UserRoleTypeEnum type) {
        String requiredRole = buildRole(type, requiredScope);
        String typeAdminRole = buildRole(type, SecurityGroup.UserRoleScopeEnum.ADMIN);
        String globalScopeRole = buildRole(SecurityGroup.UserRoleTypeEnum.GLOBAL, requiredScope);
        String globalAdminRole = buildRole(SecurityGroup.UserRoleTypeEnum.GLOBAL, SecurityGroup.UserRoleScopeEnum.ADMIN);

        return user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals(requiredRole)
                        || role.equals(typeAdminRole)
                        || role.equals(globalScopeRole)
                        || role.equals(globalAdminRole));
    }

    public boolean hasAccess(User user, SecurityGroup.UserRoleScopeEnum requiredScope, MapOverlay mapOverlay) {
        if (hasScope(user, SecurityGroup.UserRoleTypeEnum.MAPOVERLAY, requiredScope)) {
            return true;
        }

        Optional<UserPermission> userPermission = userPermissionsRepository.findByUserAndMapOverlay(user, mapOverlay);
        if (userPermission.isPresent()) {
            if (testScope(requiredScope, userPermission.get().getScope())) {
                return true;
            }
        } else {
            for (SecurityGroup sg : user.getSecurityGroups()) {
                Optional<SecurityGroupPermission> sgPermission =
                        securityGroupPermissionsRepository.findBySecurityGroupAndMapOverlay(sg, mapOverlay);
                if (sgPermission.isPresent()) {
                    if (testScope(requiredScope, sgPermission.get().getScope())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean hasAccess(User user, SecurityGroup.UserRoleScopeEnum requiredScope, Unit unit) {
        if (hasScope(user, SecurityGroup.UserRoleTypeEnum.UNIT, requiredScope)) {
            return true;
        }
        if (requiredScope == SecurityGroup.UserRoleScopeEnum.VIEW && user.getUnit().getId().equals(unit.getId())) {
            return true;
        }

        Optional<UserPermission> userPermission = userPermissionsRepository.findByUserAndUnit(user, unit);
        if (userPermission.isPresent()) {
            return testScope(requiredScope, userPermission.get().getScope());
        } else {
            for (SecurityGroup sg : user.getSecurityGroups()) {
                Optional<SecurityGroupPermission> sgPermission = securityGroupPermissionsRepository.findBySecurityGroupAndUnit(sg, unit);
                if (sgPermission.isPresent()) {
                    if (testScope(requiredScope, sgPermission.get().getScope())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean hasAccess(User user, SecurityGroup.UserRoleScopeEnum requiredScope, MissionGroup missionGroup) {
        if (hasScope(user, SecurityGroup.UserRoleTypeEnum.MISSIONGROUP, requiredScope)) {
            return true;
        }
        if (requiredScope == SecurityGroup.UserRoleScopeEnum.VIEW) {
            if (user.getUnit() != null) {
                return missionGroup.getUnits().stream().anyMatch(unit -> unit.getId().equals(user.getUnit().getId()));
            }
        }
        return false;
    }

    public boolean hasAccess(User user, SecurityGroup.UserRoleScopeEnum requiredScope, MapItem mapItem) {
        if (hasScope(user, SecurityGroup.UserRoleTypeEnum.MAPITEM, requiredScope)) {
            return true;
        }

        Optional<UserPermission> userPermission = userPermissionsRepository.findByUserAndMapItem(user, mapItem);
        if (userPermission.isPresent()) {
            return testScope(requiredScope, userPermission.get().getScope());
        } else {
            for (SecurityGroup sg : user.getSecurityGroups()) {
                Optional<SecurityGroupPermission> sgPermission =
                        securityGroupPermissionsRepository.findBySecurityGroupAndMapItem(sg, mapItem);
                if (sgPermission.isPresent()) {
                    if (testScope(requiredScope, sgPermission.get().getScope())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean hasAccess(User user, SecurityGroup.UserRoleScopeEnum requiredScope, MapGroup mapGroup) {
        if (hasScope(user, SecurityGroup.UserRoleTypeEnum.MAPGROUP, requiredScope)) {
            return true;
        }

        Optional<UserPermission> userPermission = userPermissionsRepository.findByUserAndMapGroup(user, mapGroup);
        if (userPermission.isPresent()) {
            if (testScope(requiredScope, userPermission.get().getScope())) {
                return true;
            }
        } else {
            for (SecurityGroup sg : user.getSecurityGroups()) {
                Optional<SecurityGroupPermission> sgPermission =
                        securityGroupPermissionsRepository.findBySecurityGroupAndMapGroup(sg, mapGroup);
                if (sgPermission.isPresent()) {
                    if (testScope(requiredScope, sgPermission.get().getScope())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean hasAccess(User user, SecurityGroup.UserRoleScopeEnum requiredScope, MapBaseLayer mapBaseLayer) {
        if (hasScope(user, SecurityGroup.UserRoleTypeEnum.MAPBASELAYER, requiredScope)) {
            return true;
        }

        Optional<UserPermission> userPermission = userPermissionsRepository.findByUserAndBaseLayer(user, mapBaseLayer);
        if (userPermission.isPresent()) {
            if (testScope(requiredScope, userPermission.get().getScope())) {
                return true;
            }
        } else {
            for (SecurityGroup sg : user.getSecurityGroups()) {
                Optional<SecurityGroupPermission> sgPermission =
                        securityGroupPermissionsRepository.findBySecurityGroupAndBaseLayer(sg, mapBaseLayer);
                if (sgPermission.isPresent()) {
                    if (testScope(requiredScope, sgPermission.get().getScope())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean hasAccess(User user, SecurityGroup.UserRoleScopeEnum requiredScope, Photo photo) {
        if (hasScope(user, SecurityGroup.UserRoleTypeEnum.PHOTO, requiredScope)) {
            return true;
        }

        Optional<UserPermission> userPermission = userPermissionsRepository.findByUserAndPhoto(user, photo);
        if (userPermission.isPresent()) {
            if (testScope(requiredScope, userPermission.get().getScope())) {
                return true;
            }
        } else {
            for (SecurityGroup sg : user.getSecurityGroups()) {
                Optional<SecurityGroupPermission> sgPermission =
                        securityGroupPermissionsRepository.findBySecurityGroupAndPhoto(sg, photo);
                if (sgPermission.isPresent()) {
                    if (testScope(requiredScope, sgPermission.get().getScope())) {
                        return true;
                    }
                }
            }
        }
        if (requiredScope == SecurityGroup.UserRoleScopeEnum.VIEW &&user.getUnit() != null && photo.getMissionGroup().getUnits().stream().anyMatch(unit -> unit.getId().equals(user.getUnit().getId()))){
            return true;
        }
        return false;
    }

    public boolean hasAccess(User user, SecurityGroup.UserRoleScopeEnum requiredScope, User checkUser) {
        if (hasAnyScope(user, SecurityGroup.UserRoleTypeEnum.USER, SecurityGroup.UserRoleScopeEnum.ADMIN)) {
            return true;
        }

        Optional<UserPermission> userPermission = userPermissionsRepository.findByUserAndEntityUser(user, checkUser);
        if (userPermission.isPresent()) {
            if (testScope(requiredScope, userPermission.get().getScope())) {
                return true;
            }
        } else {
            for (SecurityGroup sg : user.getSecurityGroups()) {
                Optional<SecurityGroupPermission> sgPermission =
                        securityGroupPermissionsRepository.findBySecurityGroupAndEntityUser(sg, checkUser);
                if (sgPermission.isPresent()) {
                    if (testScope(requiredScope, sgPermission.get().getScope())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean testScope(SecurityGroup.UserRoleScopeEnum requiredScope, SecurityGroup.UserRoleScopeEnum providedScope) {
        return switch (requiredScope) {
            case VIEW -> isView(providedScope);
            case EDIT -> isEdit(providedScope);
            case CREATE -> isCreate(providedScope);
            case DELETE -> isDelete(providedScope);
            case ADMIN -> providedScope == SecurityGroup.UserRoleScopeEnum.ADMIN;
        };
    }

    public static boolean isView(SecurityGroup.UserRoleScopeEnum toTest) {
        return toTest == SecurityGroup.UserRoleScopeEnum.VIEW || toTest == SecurityGroup.UserRoleScopeEnum.EDIT || toTest == SecurityGroup.UserRoleScopeEnum.ADMIN;
    }

    public static boolean isEdit(SecurityGroup.UserRoleScopeEnum toTest) {
        return toTest == SecurityGroup.UserRoleScopeEnum.VIEW || toTest == SecurityGroup.UserRoleScopeEnum.EDIT || toTest == SecurityGroup.UserRoleScopeEnum.ADMIN;
    }

    public static boolean isCreate(SecurityGroup.UserRoleScopeEnum toTest) {
        return toTest == SecurityGroup.UserRoleScopeEnum.CREATE || toTest == SecurityGroup.UserRoleScopeEnum.ADMIN;
    }

    public static boolean isDelete(SecurityGroup.UserRoleScopeEnum toTest) {
        return toTest == SecurityGroup.UserRoleScopeEnum.DELETE || toTest == SecurityGroup.UserRoleScopeEnum.ADMIN;
    }


    public static boolean hasAnyScope(User user, SecurityGroup.UserRoleTypeEnum type,
                                      SecurityGroup.UserRoleScopeEnum... scopes) {
        if (scopes == null || scopes.length == 0) {
            return false;
        }
        return Arrays.stream(scopes).anyMatch(scope -> hasScope(user, type, scope));
    }

    private static boolean hasScope(User user, SecurityGroup.UserRoleTypeEnum type, SecurityGroup.UserRoleScopeEnum scope) {
        if (user == null || type == null || scope == null) {
            return false;
        }
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        String requiredRole = buildRole(type, scope);
        String typeAdminRole = buildRole(type, SecurityGroup.UserRoleScopeEnum.ADMIN);
        String globalScopeRole = buildRole(SecurityGroup.UserRoleTypeEnum.GLOBAL, scope);
        String globalAdminRole = buildRole(SecurityGroup.UserRoleTypeEnum.GLOBAL, SecurityGroup.UserRoleScopeEnum.ADMIN);

        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals(requiredRole)
                        || role.equals(typeAdminRole)
                        || role.equals(globalScopeRole)
                        || role.equals(globalAdminRole));
    }

    private static String buildRole(SecurityGroup.UserRoleTypeEnum type, SecurityGroup.UserRoleScopeEnum scope) {
        return "ROLE_" + type.name() + "_" + scope.name();
    }

    public List<MapOverlay> getMapOverlaysForUser(User userDetails) {
        ArrayList<MapOverlay> permittedOverlays = new ArrayList<>(this.userPermissionsRepository.findByUserAndMapOverlayNotNull(userDetails).stream().map(UserPermission::getMapOverlay).toList());
        for (SecurityGroup sg : userDetails.getSecurityGroups()) {
            permittedOverlays.addAll(this.securityGroupPermissionsRepository.findBySecurityGroupAndMapOverlayNotNull(sg).stream().map(SecurityGroupPermission::getMapOverlay).toList());
        }
        if (userDetails.getUnit() != null) {
            if (userDetails.getUnit().getMissionGroup() != null) {
                userDetails.getUnit().getMissionGroup().getMapGroups().stream().forEach(group -> permittedOverlays.addAll(mapOverlayRepository.findByMapGroup(group)));
            }
        }
        return permittedOverlays.stream().distinct().toList();
    }

    public List<Unit> getUnitsForUser(User userDetails) {
        ArrayList<Unit> permittedOverlays = new ArrayList<>(this.userPermissionsRepository.findByUserAndUnitNotNull(userDetails).stream().map(UserPermission::getUnit).toList());
        for (SecurityGroup sg : userDetails.getSecurityGroups()) {
            permittedOverlays.addAll(this.securityGroupPermissionsRepository.findBySecurityGroupAndUnitNotNull(sg).stream().map(SecurityGroupPermission::getUnit).toList());
        }
        if (userDetails.getUnit() != null) {
            permittedOverlays.add(userDetails.getUnit());
        }
        return permittedOverlays.stream().distinct().toList();
    }

    public List<MapItem> getMapItemsForUser(User userDetails) {
        ArrayList<MapItem> permittedItems = new ArrayList<>(this.userPermissionsRepository.findByUserAndMapItemNotNull(userDetails).stream().map(UserPermission::getMapItem).toList());
        for (SecurityGroup sg : userDetails.getSecurityGroups()) {
            permittedItems.addAll(this.securityGroupPermissionsRepository.findBySecurityGroupAndMapItemNotNull(sg).stream().map(SecurityGroupPermission::getMapItem).toList());
        }
        List<MapGroup> permittedGroups = getMapGroupsForUser(userDetails);
        for (MapGroup mg : permittedGroups) {
            permittedItems.addAll(mg.getMapItems());
        }
        return permittedItems.stream().distinct().toList();
    }

    public List<MapGroup> getMapGroupsForUser(User userDetails) {
        ArrayList<MapGroup> permittedGroups = new ArrayList<>(this.userPermissionsRepository.findByUserAndMapGroupNotNull(userDetails).stream().map(UserPermission::getMapGroup).toList());
        for (SecurityGroup sg : userDetails.getSecurityGroups()) {
            permittedGroups.addAll(this.securityGroupPermissionsRepository.findBySecurityGroupAndMapGroupNotNull(sg).stream().map(SecurityGroupPermission::getMapGroup).toList());
        }
        if (userDetails.getUnit() != null) {
            if (userDetails.getUnit().getMissionGroup() != null) {
                permittedGroups.addAll(userDetails.getUnit().getMissionGroup().getMapGroups());
            }
        }
        return permittedGroups.stream().distinct().toList();
    }
    public List<MapBaseLayer> getMapBaseLayersForUser(User userDetails) {
        ArrayList<MapBaseLayer> permittedOverlays = new ArrayList<>(this.userPermissionsRepository.findByUserAndBaseLayerNotNull(userDetails).stream().map(UserPermission::getBaseLayer).toList());
        for (SecurityGroup sg : userDetails.getSecurityGroups()) {
            permittedOverlays.addAll(this.securityGroupPermissionsRepository.findBySecurityGroupAndBaseLayerNotNull(sg).stream().map(SecurityGroupPermission::getBaseLayer).toList());
        }
        return permittedOverlays.stream().distinct().toList();
    }
    public List<User> getUsersForUser(User userDetails) {
        ArrayList<User> permittedOverlays = new ArrayList<>(this.userPermissionsRepository.findByUserAndEntityUserNotNull(userDetails).stream().map(UserPermission::getEntityUser).toList());
        for (SecurityGroup sg : userDetails.getSecurityGroups()) {
            permittedOverlays.addAll(this.securityGroupPermissionsRepository.findBySecurityGroupAndEntityUserNotNull(sg).stream().map(SecurityGroupPermission::getEntityUser).toList());
        }
        permittedOverlays.add(userDetails);
        return permittedOverlays.stream().distinct().toList();
    }
    public List<Photo> getPhotosForUser(User userDetails) {
        ArrayList<Photo> permittedPhotos = new ArrayList<>(this.userPermissionsRepository.findByUserAndPhotoNotNull(userDetails).stream().map(UserPermission::getPhoto).toList());
        for (SecurityGroup sg : userDetails.getSecurityGroups()) {
            permittedPhotos.addAll(this.securityGroupPermissionsRepository.findBySecurityGroupAndPhotoNotNull(sg).stream().map(SecurityGroupPermission::getPhoto).toList());
        }

        getMissionGroupsForUser(userDetails).forEach(group -> permittedPhotos.addAll(photoRepository.findByMissionGroup(group)));
        return permittedPhotos.stream().distinct().toList();
    }

    public List<MissionGroup> getMissionGroupsForUser(User userDetails) {
        ArrayList<MissionGroup> permittedMissionGroups = new ArrayList<>(this.userPermissionsRepository.findByUserAndMissionGroupNotNull(userDetails).stream().map(UserPermission::getMissionGroup).toList());
        for (SecurityGroup sg : userDetails.getSecurityGroups()) {
            permittedMissionGroups.addAll(this.securityGroupPermissionsRepository.findBySecurityGroupAndMissionGroupNotNull(sg).stream().map(SecurityGroupPermission::getMissionGroup).toList());
        }
        if (userDetails.getUnit() != null) {
            if (userDetails.getUnit().getMissionGroup() != null) {
                permittedMissionGroups.add(userDetails.getUnit().getMissionGroup());
            }
        }
        return permittedMissionGroups.stream().distinct().toList();
    }
}
