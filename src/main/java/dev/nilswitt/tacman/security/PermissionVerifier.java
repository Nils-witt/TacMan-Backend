package dev.nilswitt.tacman.security;

import dev.nilswitt.tacman.entities.*;
import dev.nilswitt.tacman.entities.services.*;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public final class PermissionVerifier {

    private final UserPermissionService userPermissionService;
    private final SecurityGroupPermissionService securityGroupPermissionService;

    @Lazy
    @Autowired
    private MapOverlayService mapOverlayService;

    @Lazy
    @Autowired
    private MapItemService mapItemService;

    @Lazy
    @Autowired
    private PhotoService photoService;

    private PermissionVerifier(
        UserPermissionService userPermissionService,
        SecurityGroupPermissionService securityGroupPermissionService
    ) {
        this.userPermissionService = userPermissionService;
        this.securityGroupPermissionService = securityGroupPermissionService;
    }

    public static boolean testScope(
        SecurityGroup.UserRoleScopeEnum requiredScope,
        SecurityGroup.UserRoleScopeEnum providedScope
    ) {
        return switch (requiredScope) {
            case VIEW -> isView(providedScope);
            case EDIT -> isEdit(providedScope);
            case CREATE -> isCreate(providedScope);
            case DELETE -> isDelete(providedScope);
            case ADMIN -> providedScope == SecurityGroup.UserRoleScopeEnum.ADMIN;
        };
    }

    public static boolean isView(SecurityGroup.UserRoleScopeEnum toTest) {
        return (
            toTest == SecurityGroup.UserRoleScopeEnum.VIEW ||
            toTest == SecurityGroup.UserRoleScopeEnum.EDIT ||
            toTest == SecurityGroup.UserRoleScopeEnum.ADMIN
        );
    }

    public static boolean isEdit(SecurityGroup.UserRoleScopeEnum toTest) {
        return (
            toTest == SecurityGroup.UserRoleScopeEnum.VIEW ||
            toTest == SecurityGroup.UserRoleScopeEnum.EDIT ||
            toTest == SecurityGroup.UserRoleScopeEnum.ADMIN
        );
    }

    public static boolean isCreate(SecurityGroup.UserRoleScopeEnum toTest) {
        return (toTest == SecurityGroup.UserRoleScopeEnum.CREATE || toTest == SecurityGroup.UserRoleScopeEnum.ADMIN);
    }

    public static boolean isDelete(SecurityGroup.UserRoleScopeEnum toTest) {
        return (toTest == SecurityGroup.UserRoleScopeEnum.DELETE || toTest == SecurityGroup.UserRoleScopeEnum.ADMIN);
    }

    public static boolean hasAnyScope(
        User user,
        SecurityGroup.UserRoleTypeEnum type,
        SecurityGroup.UserRoleScopeEnum... scopes
    ) {
        if (scopes == null || scopes.length == 0) {
            return false;
        }
        return Arrays.stream(scopes).anyMatch(scope -> hasScope(user, type, scope));
    }

    private static boolean hasScope(
        User user,
        SecurityGroup.UserRoleTypeEnum type,
        SecurityGroup.UserRoleScopeEnum scope
    ) {
        if (user == null || type == null || scope == null) {
            return false;
        }
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        String requiredRole = buildRole(type, scope);
        String typeAdminRole = buildRole(type, SecurityGroup.UserRoleScopeEnum.ADMIN);
        String globalScopeRole = buildRole(SecurityGroup.UserRoleTypeEnum.GLOBAL, scope);
        String globalAdminRole = buildRole(
            SecurityGroup.UserRoleTypeEnum.GLOBAL,
            SecurityGroup.UserRoleScopeEnum.ADMIN
        );

        return authorities
            .stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(
                role ->
                    role.equals(requiredRole) ||
                    role.equals(typeAdminRole) ||
                    role.equals(globalScopeRole) ||
                    role.equals(globalAdminRole)
            );
    }

    private static String buildRole(SecurityGroup.UserRoleTypeEnum type, SecurityGroup.UserRoleScopeEnum scope) {
        return "ROLE_" + type.name() + "_" + scope.name();
    }

    public boolean hasAccess(
        User user,
        SecurityGroup.UserRoleScopeEnum requiredScope,
        SecurityGroup.UserRoleTypeEnum type
    ) {
        String requiredRole = buildRole(type, requiredScope);
        String typeAdminRole = buildRole(type, SecurityGroup.UserRoleScopeEnum.ADMIN);
        String globalScopeRole = buildRole(SecurityGroup.UserRoleTypeEnum.GLOBAL, requiredScope);
        String globalAdminRole = buildRole(
            SecurityGroup.UserRoleTypeEnum.GLOBAL,
            SecurityGroup.UserRoleScopeEnum.ADMIN
        );

        return user
            .getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(
                role ->
                    role.equals(requiredRole) ||
                    role.equals(typeAdminRole) ||
                    role.equals(globalScopeRole) ||
                    role.equals(globalAdminRole)
            );
    }

    public boolean hasAccess(User user, SecurityGroup.UserRoleScopeEnum requiredScope, MapOverlay mapOverlay) {
        if (hasScope(user, SecurityGroup.UserRoleTypeEnum.MAPOVERLAY, requiredScope)) {
            return true;
        }

        return getScopes(mapOverlay, user).contains(requiredScope);
    }

    public boolean hasAccess(User user, SecurityGroup.UserRoleScopeEnum requiredScope, Unit unit) {
        if (hasScope(user, SecurityGroup.UserRoleTypeEnum.UNIT, requiredScope)) {
            return true;
        }

        return getScopes(unit, user).contains(requiredScope);
    }

    public boolean hasAccess(User user, SecurityGroup.UserRoleScopeEnum requiredScope, MissionGroup missionGroup) {
        if (hasScope(user, SecurityGroup.UserRoleTypeEnum.MISSIONGROUP, requiredScope)) {
            return true;
        }
        return getScopes(missionGroup, user).contains(requiredScope);
    }

    public boolean hasAccess(User user, SecurityGroup.UserRoleScopeEnum requiredScope, MapItem mapItem) {
        if (hasScope(user, SecurityGroup.UserRoleTypeEnum.MAPITEM, requiredScope)) {
            return true;
        }

        return getScopes(mapItem, user).contains(requiredScope);
    }

    public boolean hasAccess(User user, SecurityGroup.UserRoleScopeEnum requiredScope, MapGroup mapGroup) {
        if (hasScope(user, SecurityGroup.UserRoleTypeEnum.MAPGROUP, requiredScope)) {
            return true;
        }
        return getScopes(mapGroup, user).contains(requiredScope);
    }

    public boolean hasAccess(User user, SecurityGroup.UserRoleScopeEnum requiredScope, MapBaseLayer mapBaseLayer) {
        if (hasScope(user, SecurityGroup.UserRoleTypeEnum.MAPBASELAYER, requiredScope)) {
            return true;
        }

        return getScopes(mapBaseLayer, user).contains(requiredScope);
    }

    public boolean hasAccess(User user, SecurityGroup.UserRoleScopeEnum requiredScope, Photo photo) {
        if (hasScope(user, SecurityGroup.UserRoleTypeEnum.PHOTO, requiredScope)) {
            return true;
        }

        return getScopes(photo, user).contains(requiredScope);
    }

    public boolean hasAccess(User user, SecurityGroup.UserRoleScopeEnum requiredScope, User checkUser) {
        if (hasAnyScope(user, SecurityGroup.UserRoleTypeEnum.USER, SecurityGroup.UserRoleScopeEnum.ADMIN)) {
            return true;
        }

        return getScopes(checkUser, user).contains(requiredScope);
    }

    public List<MapOverlay> getMapOverlaysForUser(User userDetails) {
        ArrayList<MapOverlay> permittedOverlays = new ArrayList<>(
            this.userPermissionService.findByUserAndMapOverlayNotNull(userDetails)
                .stream()
                .map(UserPermission::getMapOverlay)
                .toList()
        );
        for (SecurityGroup sg : userDetails.getSecurityGroups()) {
            permittedOverlays.addAll(
                this.securityGroupPermissionService.findBySecurityGroupAndMapOverlayNotNull(sg)
                    .stream()
                    .map(SecurityGroupPermission::getMapOverlay)
                    .toList()
            );
        }
        if (userDetails.getUnit() != null) {
            if (userDetails.getUnit().getMissionGroup() != null) {
                userDetails
                    .getUnit()
                    .getMissionGroup()
                    .getMapGroups()
                    .stream()
                    .forEach(group -> permittedOverlays.addAll(mapOverlayService.findByMapGroup(group)));
            }
        }
        return permittedOverlays.stream().distinct().toList();
    }

    public List<Unit> getUnitsForUser(User userDetails) {
        ArrayList<Unit> permittedUnits = new ArrayList<>(
            this.userPermissionService.findByUserAndUnitNotNull(userDetails)
                .stream()
                .map(UserPermission::getUnit)
                .toList()
        );
        for (SecurityGroup sg : userDetails.getSecurityGroups()) {
            permittedUnits.addAll(
                this.securityGroupPermissionService.findBySecurityGroupAndUnitNotNull(sg)
                    .stream()
                    .map(SecurityGroupPermission::getUnit)
                    .toList()
            );
        }
        if (userDetails.getUnit() != null) {
            permittedUnits.add(userDetails.getUnit());
        }
        return permittedUnits.stream().distinct().toList();
    }

    public List<MapItem> getMapItemsForUser(User userDetails) {
        ArrayList<MapItem> permittedItems = new ArrayList<>(
            this.userPermissionService.findByUserAndMapItemNotNull(userDetails)
                .stream()
                .map(UserPermission::getMapItem)
                .toList()
        );
        for (SecurityGroup sg : userDetails.getSecurityGroups()) {
            permittedItems.addAll(
                this.securityGroupPermissionService.findBySecurityGroupAndMapItemNotNull(sg)
                    .stream()
                    .map(SecurityGroupPermission::getMapItem)
                    .toList()
            );
        }
        List<MapGroup> permittedGroups = getMapGroupsForUser(userDetails);
        for (MapGroup mg : permittedGroups) {
            permittedItems.addAll(mapItemService.findByMapGroup(mg));
        }
        return permittedItems.stream().distinct().toList();
    }

    public List<MapGroup> getMapGroupsForUser(User userDetails) {
        ArrayList<MapGroup> permittedGroups = new ArrayList<>(
            this.userPermissionService.findByUserAndMapGroupNotNull(userDetails)
                .stream()
                .map(UserPermission::getMapGroup)
                .toList()
        );
        for (SecurityGroup sg : userDetails.getSecurityGroups()) {
            permittedGroups.addAll(
                this.securityGroupPermissionService.findBySecurityGroupAndMapGroupNotNull(sg)
                    .stream()
                    .map(SecurityGroupPermission::getMapGroup)
                    .toList()
            );
        }
        if (userDetails.getUnit() != null) {
            if (userDetails.getUnit().getMissionGroup() != null) {
                permittedGroups.addAll(userDetails.getUnit().getMissionGroup().getMapGroups());
            }
        }
        return permittedGroups.stream().distinct().toList();
    }

    public List<MapBaseLayer> getMapBaseLayersForUser(User userDetails) {
        ArrayList<MapBaseLayer> permittedOverlays = new ArrayList<>(
            this.userPermissionService.findByUserAndBaseLayerNotNull(userDetails)
                .stream()
                .map(UserPermission::getBaseLayer)
                .toList()
        );
        for (SecurityGroup sg : userDetails.getSecurityGroups()) {
            permittedOverlays.addAll(
                this.securityGroupPermissionService.findBySecurityGroupAndBaseLayerNotNull(sg)
                    .stream()
                    .map(SecurityGroupPermission::getBaseLayer)
                    .toList()
            );
        }
        return permittedOverlays.stream().distinct().toList();
    }

    public List<User> getUsersForUser(User userDetails) {
        ArrayList<User> permittedOverlays = new ArrayList<>(
            this.userPermissionService.findByUserAndEntityUserNotNull(userDetails)
                .stream()
                .map(UserPermission::getEntityUser)
                .toList()
        );
        for (SecurityGroup sg : userDetails.getSecurityGroups()) {
            permittedOverlays.addAll(
                this.securityGroupPermissionService.findBySecurityGroupAndEntityUserNotNull(sg)
                    .stream()
                    .map(SecurityGroupPermission::getEntityUser)
                    .toList()
            );
        }
        permittedOverlays.add(userDetails);
        return permittedOverlays.stream().distinct().toList();
    }

    public List<Photo> getPhotosForUser(User userDetails) {
        ArrayList<Photo> permittedPhotos = new ArrayList<>(
            this.userPermissionService.findByUserAndPhotoNotNull(userDetails)
                .stream()
                .map(UserPermission::getPhoto)
                .toList()
        );
        for (SecurityGroup sg : userDetails.getSecurityGroups()) {
            permittedPhotos.addAll(
                this.securityGroupPermissionService.findBySecurityGroupAndPhotoNotNull(sg)
                    .stream()
                    .map(SecurityGroupPermission::getPhoto)
                    .toList()
            );
        }

        getMissionGroupsForUser(userDetails).forEach(group ->
            permittedPhotos.addAll(photoService.findByMissionGroup(group))
        );
        return permittedPhotos.stream().distinct().toList();
    }

    public List<MissionGroup> getMissionGroupsForUser(User userDetails) {
        ArrayList<MissionGroup> permittedMissionGroups = new ArrayList<>(
            this.userPermissionService.findByUserAndMissionGroupNotNull(userDetails)
                .stream()
                .map(UserPermission::getMissionGroup)
                .toList()
        );
        for (SecurityGroup sg : userDetails.getSecurityGroups()) {
            permittedMissionGroups.addAll(
                this.securityGroupPermissionService.findBySecurityGroupAndMissionGroupNotNull(sg)
                    .stream()
                    .map(SecurityGroupPermission::getMissionGroup)
                    .toList()
            );
        }
        if (userDetails.getUnit() != null) {
            if (userDetails.getUnit().getMissionGroup() != null) {
                permittedMissionGroups.add(userDetails.getUnit().getMissionGroup());
            }
        }
        return permittedMissionGroups.stream().distinct().toList();
    }

    public Set<SecurityGroup.UserRoleScopeEnum> getScopes(AbstractEntity entity, User user) {
        return switch (entity) {
            case Unit unit -> getScopes(unit, user);
            case MissionGroup missionGroup -> getScopes(missionGroup, user);
            case User user1 -> getScopes(user1, user);
            case Photo photo -> getScopes(photo, user);
            case MapOverlay mapOverlay -> getScopes(mapOverlay, user);
            case MapItem mapItem -> getScopes(mapItem, user);
            case MapBaseLayer mapBaseLayer -> getScopes(mapBaseLayer, user);
            case MapGroup mapGroup -> getScopes(mapGroup, user);
            case null, default -> Set.of();
        };
    }

    public Set<SecurityGroup.UserRoleScopeEnum> getScopes(Unit unit, User user) {
        HashSet<SecurityGroup.UserRoleScopeEnum> scopes = new HashSet<>();

        if (user.getUnit() != null && user.getUnit().getId().equals(unit.getId())) {
            scopes.add(SecurityGroup.UserRoleScopeEnum.VIEW);
        }

        for (SecurityGroup.UserRoleScopeEnum scope : SecurityGroup.UserRoleScopeEnum.values()) {
            if (hasScope(user, SecurityGroup.UserRoleTypeEnum.UNIT, scope)) {
                scopes.add(scope);
            }
        }

        this.userPermissionService.findByUserAndUnit(user, unit).ifPresent(perm -> scopes.add(perm.getScope()));
        for (SecurityGroup sg : user.getSecurityGroups()) {
            this.securityGroupPermissionService.findBySecurityGroupAndUnit(sg, unit)
                .stream()
                .map(SecurityGroupPermission::getScope)
                .forEach(scopes::add);
        }

        return scopes;
    }

    public Set<SecurityGroup.UserRoleScopeEnum> getScopes(MissionGroup missionGroup, User user) {
        HashSet<SecurityGroup.UserRoleScopeEnum> scopes = new HashSet<>();

        // direct permissions
        for (SecurityGroup.UserRoleScopeEnum scope : SecurityGroup.UserRoleScopeEnum.values()) {
            if (hasScope(user, SecurityGroup.UserRoleTypeEnum.MISSIONGROUP, scope)) {
                scopes.add(scope);
            }
        }

        this.userPermissionService.findByUserAndMissionGroup(user, missionGroup).ifPresent(perm ->
            scopes.add(perm.getScope())
        );
        for (SecurityGroup sg : user.getSecurityGroups()) {
            this.securityGroupPermissionService.findBySecurityGroupAndMissionGroup(sg, missionGroup)
                .stream()
                .map(SecurityGroupPermission::getScope)
                .forEach(scopes::add);
        }
        // -- Custom addion based on relations
        if (user.getUnit() != null) {
            if (
                missionGroup
                    .getUnits()
                    .stream()
                    .anyMatch(unit -> unit.getId().equals(user.getUnit().getId()))
            ) {
                scopes.add(SecurityGroup.UserRoleScopeEnum.VIEW);
            }
        }
        return scopes;
    }

    public Set<SecurityGroup.UserRoleScopeEnum> getScopes(User target, User user) {
        HashSet<SecurityGroup.UserRoleScopeEnum> scopes = new HashSet<>();

        // direct permissions
        for (SecurityGroup.UserRoleScopeEnum scope : SecurityGroup.UserRoleScopeEnum.values()) {
            if (hasScope(user, SecurityGroup.UserRoleTypeEnum.MISSIONGROUP, scope)) {
                scopes.add(scope);
            }
        }

        this.userPermissionService.findByUserAndEntityUser(user, target).ifPresent(perm -> scopes.add(perm.getScope()));
        for (SecurityGroup sg : user.getSecurityGroups()) {
            this.securityGroupPermissionService.findBySecurityGroupAndEntityUser(sg, target)
                .stream()
                .map(SecurityGroupPermission::getScope)
                .forEach(scopes::add);
        }

        // -- Custom addion based on relations

        return scopes;
    }

    public Set<SecurityGroup.UserRoleScopeEnum> getScopes(Photo target, User user) {
        HashSet<SecurityGroup.UserRoleScopeEnum> scopes = new HashSet<>();

        // direct permissions
        for (SecurityGroup.UserRoleScopeEnum scope : SecurityGroup.UserRoleScopeEnum.values()) {
            if (hasScope(user, SecurityGroup.UserRoleTypeEnum.MISSIONGROUP, scope)) {
                scopes.add(scope);
            }
        }

        this.userPermissionService.findByUserAndPhoto(user, target).ifPresent(perm -> scopes.add(perm.getScope()));
        for (SecurityGroup sg : user.getSecurityGroups()) {
            this.securityGroupPermissionService.findBySecurityGroupAndPhoto(sg, target)
                .stream()
                .map(SecurityGroupPermission::getScope)
                .forEach(scopes::add);
        }

        // -- Custom addion based on relations

        if (
            user.getUnit() != null &&
            target
                .getMissionGroup()
                .getUnits()
                .stream()
                .anyMatch(unit -> unit.getId().equals(user.getUnit().getId()))
        ) {
            scopes.add(SecurityGroup.UserRoleScopeEnum.VIEW);
        }
        return scopes;
    }

    public Set<SecurityGroup.UserRoleScopeEnum> getScopes(MapOverlay target, User user) {
        HashSet<SecurityGroup.UserRoleScopeEnum> scopes = new HashSet<>();

        // direct permissions
        for (SecurityGroup.UserRoleScopeEnum scope : SecurityGroup.UserRoleScopeEnum.values()) {
            if (hasScope(user, SecurityGroup.UserRoleTypeEnum.MAPOVERLAY, scope)) {
                scopes.add(scope);
            }
        }

        this.userPermissionService.findByUserAndMapOverlay(user, target).ifPresent(perm -> scopes.add(perm.getScope()));
        for (SecurityGroup sg : user.getSecurityGroups()) {
            this.securityGroupPermissionService.findBySecurityGroupAndMapOverlay(sg, target)
                .stream()
                .map(SecurityGroupPermission::getScope)
                .forEach(scopes::add);
        }

        if (user.getUnit() != null && user.getUnit().getMissionGroup() != null) {
            if (
                user
                    .getUnit()
                    .getMissionGroup()
                    .getMapGroups()
                    .stream()
                    .anyMatch(map -> map.getId().equals(target.getMapGroup().getId()))
            ) {
                scopes.add(SecurityGroup.UserRoleScopeEnum.VIEW);
            }
        }

        return scopes;
    }

    public Set<SecurityGroup.UserRoleScopeEnum> getScopes(MapItem target, User user) {
        HashSet<SecurityGroup.UserRoleScopeEnum> scopes = new HashSet<>();

        // direct permissions
        for (SecurityGroup.UserRoleScopeEnum scope : SecurityGroup.UserRoleScopeEnum.values()) {
            if (hasScope(user, SecurityGroup.UserRoleTypeEnum.MAPITEM, scope)) {
                scopes.add(scope);
            }
        }

        this.userPermissionService.findByUserAndMapItem(user, target).ifPresent(perm -> scopes.add(perm.getScope()));
        for (SecurityGroup sg : user.getSecurityGroups()) {
            this.securityGroupPermissionService.findBySecurityGroupAndMapItem(sg, target)
                .stream()
                .map(SecurityGroupPermission::getScope)
                .forEach(scopes::add);
        }

        if (user.getUnit() != null && user.getUnit().getMissionGroup() != null) {
            if (
                user
                    .getUnit()
                    .getMissionGroup()
                    .getMapGroups()
                    .stream()
                    .anyMatch(map -> map.getId().equals(target.getMapGroup().getId()))
            ) {
                scopes.add(SecurityGroup.UserRoleScopeEnum.VIEW);
            }
        }
        return scopes;
    }

    public Set<SecurityGroup.UserRoleScopeEnum> getScopes(MapBaseLayer target, User user) {
        HashSet<SecurityGroup.UserRoleScopeEnum> scopes = new HashSet<>();

        // direct permissions
        for (SecurityGroup.UserRoleScopeEnum scope : SecurityGroup.UserRoleScopeEnum.values()) {
            if (hasScope(user, SecurityGroup.UserRoleTypeEnum.MAPITEM, scope)) {
                scopes.add(scope);
            }
        }

        this.userPermissionService.findByUserAndBaseLayer(user, target).ifPresent(perm -> scopes.add(perm.getScope()));
        for (SecurityGroup sg : user.getSecurityGroups()) {
            this.securityGroupPermissionService.findBySecurityGroupAndBaseLayer(sg, target)
                .stream()
                .map(SecurityGroupPermission::getScope)
                .forEach(scopes::add);
        }

        return scopes;
    }

    public Set<SecurityGroup.UserRoleScopeEnum> getScopes(MapGroup target, User user) {
        HashSet<SecurityGroup.UserRoleScopeEnum> scopes = new HashSet<>();

        // direct permissions
        for (SecurityGroup.UserRoleScopeEnum scope : SecurityGroup.UserRoleScopeEnum.values()) {
            if (hasScope(user, SecurityGroup.UserRoleTypeEnum.MAPITEM, scope)) {
                scopes.add(scope);
            }
        }

        this.userPermissionService.findByUserAndMapGroup(user, target).ifPresent(perm -> scopes.add(perm.getScope()));
        for (SecurityGroup sg : user.getSecurityGroups()) {
            this.securityGroupPermissionService.findBySecurityGroupAndMapGroup(sg, target)
                .stream()
                .map(SecurityGroupPermission::getScope)
                .forEach(scopes::add);
        }

        // custom relations

        if (user.getUnit() != null && user.getUnit().getMissionGroup() != null) {
            if (
                user
                    .getUnit()
                    .getMissionGroup()
                    .getMapGroups()
                    .stream()
                    .anyMatch(map -> map.getId().equals(target.getId()))
            ) {
                scopes.add(SecurityGroup.UserRoleScopeEnum.VIEW);
            }
        }

        return scopes;
    }
}
