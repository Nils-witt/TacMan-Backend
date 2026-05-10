package dev.nilswitt.tacman.entities.services;

import dev.nilswitt.tacman.entities.*;
import dev.nilswitt.tacman.entities.repositories.SecurityGroupPermissionsRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class SecurityGroupPermissionService {

    private final SecurityGroupPermissionsRepository securityGroupPermissionsRepository;

    public SecurityGroupPermissionService(SecurityGroupPermissionsRepository securityGroupPermissionsRepository) {
        this.securityGroupPermissionsRepository = securityGroupPermissionsRepository;
    }

    public List<SecurityGroupPermission> findAll() {
        return securityGroupPermissionsRepository.findAll();
    }

    public Optional<SecurityGroupPermission> findById(UUID id) {
        return securityGroupPermissionsRepository.findById(id);
    }

    public SecurityGroupPermission save(SecurityGroupPermission permission) {
        return securityGroupPermissionsRepository.save(permission);
    }

    public void deleteById(UUID id) {
        securityGroupPermissionsRepository.deleteById(id);
    }

    public void delete(SecurityGroupPermission permission) {
        securityGroupPermissionsRepository.delete(permission);
    }

    public void deleteBySecurityGroup(SecurityGroup securityGroup) {
        securityGroupPermissionsRepository.deleteBySecurityGroup(securityGroup);
    }

    public List<SecurityGroupPermission> findByMapOverlay(MapOverlay mapOverlay) {
        return securityGroupPermissionsRepository.findByMapOverlay(mapOverlay);
    }

    public List<SecurityGroupPermission> findByMapItem(MapItem mapItem) {
        return securityGroupPermissionsRepository.findByMapItem(mapItem);
    }

    public List<SecurityGroupPermission> findByMapGroup(MapGroup mapGroup) {
        return securityGroupPermissionsRepository.findByMapGroup(mapGroup);
    }

    public List<SecurityGroupPermission> findByBaseLayer(MapBaseLayer mapBaseLayer) {
        return securityGroupPermissionsRepository.findByBaseLayer(mapBaseLayer);
    }

    public List<SecurityGroupPermission> findByUnit(Unit unit) {
        return securityGroupPermissionsRepository.findByUnit(unit);
    }

    public List<SecurityGroupPermission> findByEntityUser(User entityUser) {
        return securityGroupPermissionsRepository.findByEntityUser(entityUser);
    }

    public List<SecurityGroupPermission> findByPhoto(Photo photo) {
        return securityGroupPermissionsRepository.findByPhoto(photo);
    }

    public List<SecurityGroupPermission> findByMissionGroup(MissionGroup missionGroup) {
        return securityGroupPermissionsRepository.findByMissionGroup(missionGroup);
    }

    public List<SecurityGroupPermission> findBySecurityGroupAndMapOverlayNotNull(SecurityGroup securityGroup) {
        return securityGroupPermissionsRepository.findBySecurityGroupAndMapOverlayNotNull(securityGroup);
    }

    public List<SecurityGroupPermission> findBySecurityGroupAndMapItemNotNull(SecurityGroup securityGroup) {
        return securityGroupPermissionsRepository.findBySecurityGroupAndMapItemNotNull(securityGroup);
    }

    public List<SecurityGroupPermission> findBySecurityGroupAndMapGroupNotNull(SecurityGroup securityGroup) {
        return securityGroupPermissionsRepository.findBySecurityGroupAndMapGroupNotNull(securityGroup);
    }

    public List<SecurityGroupPermission> findBySecurityGroupAndBaseLayerNotNull(SecurityGroup securityGroup) {
        return securityGroupPermissionsRepository.findBySecurityGroupAndBaseLayerNotNull(securityGroup);
    }

    public List<SecurityGroupPermission> findBySecurityGroupAndUnitNotNull(SecurityGroup securityGroup) {
        return securityGroupPermissionsRepository.findBySecurityGroupAndUnitNotNull(securityGroup);
    }

    public List<SecurityGroupPermission> findBySecurityGroupAndEntityUserNotNull(SecurityGroup securityGroup) {
        return securityGroupPermissionsRepository.findBySecurityGroupAndEntityUserNotNull(securityGroup);
    }

    public List<SecurityGroupPermission> findBySecurityGroupAndPhotoNotNull(SecurityGroup securityGroup) {
        return securityGroupPermissionsRepository.findBySecurityGroupAndPhotoNotNull(securityGroup);
    }

    public List<SecurityGroupPermission> findBySecurityGroupAndMissionGroupNotNull(SecurityGroup securityGroup) {
        return securityGroupPermissionsRepository.findBySecurityGroupAndMissionGroupNotNull(securityGroup);
    }

    public Optional<SecurityGroupPermission> findBySecurityGroupAndMapItem(
        SecurityGroup securityGroup,
        MapItem mapItem
    ) {
        return securityGroupPermissionsRepository.findBySecurityGroupAndMapItem(securityGroup, mapItem);
    }

    public Optional<SecurityGroupPermission> findBySecurityGroupAndMapGroup(
        SecurityGroup securityGroup,
        MapGroup mapGroup
    ) {
        return securityGroupPermissionsRepository.findBySecurityGroupAndMapGroup(securityGroup, mapGroup);
    }

    public Optional<SecurityGroupPermission> findBySecurityGroupAndMapOverlay(
        SecurityGroup securityGroup,
        MapOverlay mapOverlay
    ) {
        return securityGroupPermissionsRepository.findBySecurityGroupAndMapOverlay(securityGroup, mapOverlay);
    }

    public Optional<SecurityGroupPermission> findBySecurityGroupAndBaseLayer(
        SecurityGroup securityGroup,
        MapBaseLayer mapBaseLayer
    ) {
        return securityGroupPermissionsRepository.findBySecurityGroupAndBaseLayer(securityGroup, mapBaseLayer);
    }

    public Optional<SecurityGroupPermission> findBySecurityGroupAndUnit(SecurityGroup securityGroup, Unit unit) {
        return securityGroupPermissionsRepository.findBySecurityGroupAndUnit(securityGroup, unit);
    }

    public Optional<SecurityGroupPermission> findBySecurityGroupAndEntityUser(
        SecurityGroup securityGroup,
        User entityUser
    ) {
        return securityGroupPermissionsRepository.findBySecurityGroupAndEntityUser(securityGroup, entityUser);
    }

    public Optional<SecurityGroupPermission> findBySecurityGroupAndPhoto(SecurityGroup securityGroup, Photo photo) {
        return securityGroupPermissionsRepository.findBySecurityGroupAndPhoto(securityGroup, photo);
    }

    public Optional<SecurityGroupPermission> findBySecurityGroupAndMissionGroup(
        SecurityGroup securityGroup,
        MissionGroup missionGroup
    ) {
        return securityGroupPermissionsRepository.findBySecurityGroupAndMissionGroup(securityGroup, missionGroup);
    }
}
