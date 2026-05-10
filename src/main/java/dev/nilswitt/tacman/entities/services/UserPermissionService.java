package dev.nilswitt.tacman.entities.services;

import dev.nilswitt.tacman.entities.*;
import dev.nilswitt.tacman.entities.repositories.UserPermissionsRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UserPermissionService {

    private final UserPermissionsRepository userPermissionsRepository;

    public UserPermissionService(UserPermissionsRepository userPermissionsRepository) {
        this.userPermissionsRepository = userPermissionsRepository;
    }

    public List<UserPermission> findAll() {
        return userPermissionsRepository.findAll();
    }

    public Optional<UserPermission> findById(UUID id) {
        return userPermissionsRepository.findById(id);
    }

    public UserPermission save(UserPermission userPermission) {
        return userPermissionsRepository.save(userPermission);
    }

    public void deleteById(UUID id) {
        userPermissionsRepository.deleteById(id);
    }

    public void delete(UserPermission userPermission) {
        userPermissionsRepository.delete(userPermission);
    }

    public List<UserPermission> findByMapOverlay(MapOverlay mapOverlay) {
        return userPermissionsRepository.findByMapOverlay(mapOverlay);
    }

    public List<UserPermission> findByMapItem(MapItem mapItem) {
        return userPermissionsRepository.findByMapItem(mapItem);
    }

    public List<UserPermission> findByMapGroup(MapGroup mapGroup) {
        return userPermissionsRepository.findByMapGroup(mapGroup);
    }

    public List<UserPermission> findByBaseLayer(MapBaseLayer mapBaseLayer) {
        return userPermissionsRepository.findByBaseLayer(mapBaseLayer);
    }

    public List<UserPermission> findByUnit(Unit unit) {
        return userPermissionsRepository.findByUnit(unit);
    }

    public List<UserPermission> findByEntityUser(User entityUser) {
        return userPermissionsRepository.findByEntityUser(entityUser);
    }

    public List<UserPermission> findByPhoto(Photo photo) {
        return userPermissionsRepository.findByPhoto(photo);
    }

    public List<UserPermission> findByMissionGroup(MissionGroup missionGroup) {
        return userPermissionsRepository.findByMissionGroup(missionGroup);
    }

    public List<UserPermission> findByUserAndMapOverlayNotNull(User user) {
        return userPermissionsRepository.findByUserAndMapOverlayNotNull(user);
    }

    public List<UserPermission> findByUserAndMapItemNotNull(User user) {
        return userPermissionsRepository.findByUserAndMapItemNotNull(user);
    }

    public List<UserPermission> findByUserAndMapGroupNotNull(User user) {
        return userPermissionsRepository.findByUserAndMapGroupNotNull(user);
    }

    public List<UserPermission> findByUserAndBaseLayerNotNull(User user) {
        return userPermissionsRepository.findByUserAndBaseLayerNotNull(user);
    }

    public List<UserPermission> findByUserAndUnitNotNull(User user) {
        return userPermissionsRepository.findByUserAndUnitNotNull(user);
    }

    public List<UserPermission> findByUserAndEntityUserNotNull(User user) {
        return userPermissionsRepository.findByUserAndEntityUserNotNull(user);
    }

    public List<UserPermission> findByUserAndPhotoNotNull(User user) {
        return userPermissionsRepository.findByUserAndPhotoNotNull(user);
    }

    public List<UserPermission> findByUserAndMissionGroupNotNull(User user) {
        return userPermissionsRepository.findByUserAndMissionGroupNotNull(user);
    }

    public Optional<UserPermission> findByUserAndMapItem(User user, MapItem mapItem) {
        return userPermissionsRepository.findByUserAndMapItem(user, mapItem);
    }

    public Optional<UserPermission> findByUserAndMapGroup(User user, MapGroup mapGroup) {
        return userPermissionsRepository.findByUserAndMapGroup(user, mapGroup);
    }

    public Optional<UserPermission> findByUserAndMapOverlay(User user, MapOverlay mapOverlay) {
        return userPermissionsRepository.findByUserAndMapOverlay(user, mapOverlay);
    }

    public Optional<UserPermission> findByUserAndBaseLayer(User user, MapBaseLayer mapBaseLayer) {
        return userPermissionsRepository.findByUserAndBaseLayer(user, mapBaseLayer);
    }

    public Optional<UserPermission> findByUserAndUnit(User user, Unit unit) {
        return userPermissionsRepository.findByUserAndUnit(user, unit);
    }

    public Optional<UserPermission> findByUserAndEntityUser(User user, User entityUser) {
        return userPermissionsRepository.findByUserAndEntityUser(user, entityUser);
    }

    public Optional<UserPermission> findByUserAndPhoto(User user, Photo photo) {
        return userPermissionsRepository.findByUserAndPhoto(user, photo);
    }

    public Optional<UserPermission> findByUserAndMissionGroup(User user, MissionGroup missionGroup) {
        return userPermissionsRepository.findByUserAndMissionGroup(user, missionGroup);
    }
}
