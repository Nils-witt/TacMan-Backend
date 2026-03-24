package dev.nilswitt.tacman.entities.repositories;

import dev.nilswitt.tacman.entities.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPermissionsRepository
  extends JpaRepository<UserPermission, UUID>
{
  List<UserPermission> findByMapOverlay(MapOverlay mapOverlay);

  List<UserPermission> findByMapItem(MapItem mapItem);

  List<UserPermission> findByMapGroup(MapGroup mapGroup);

  List<UserPermission> findByBaseLayer(MapBaseLayer mapBaseLayer);

  List<UserPermission> findByUnit(Unit unit);

  List<UserPermission> findByEntityUser(User entityUser);

  List<UserPermission> findByUserAndMapOverlayNotNull(User user);

  List<UserPermission> findByUserAndMapItemNotNull(User user);

  List<UserPermission> findByUserAndMapGroupNotNull(User user);

  List<UserPermission> findByUserAndBaseLayerNotNull(User user);

  List<UserPermission> findByUserAndUnitNotNull(User user);

  List<UserPermission> findByUserAndEntityUserNotNull(User user);

  List<UserPermission> findByUserAndPhotoNotNull(User user);

  List<UserPermission> findByUserAndMissionGroupNotNull(User user);

  Optional<UserPermission> findByUserAndMapItem(User user, MapItem mapItem);

  Optional<UserPermission> findByUserAndMapGroup(User user, MapGroup mapGroup);

  Optional<UserPermission> findByUserAndMapOverlay(
    User user,
    MapOverlay mapOverlay
  );

  Optional<UserPermission> findByUserAndBaseLayer(
    User user,
    MapBaseLayer mapBaseLayer
  );

  Optional<UserPermission> findByUserAndUnit(User user, Unit unit);

  Optional<UserPermission> findByUserAndEntityUser(User user, User entityUser);

  Optional<UserPermission> findByUserAndPhoto(User user, Photo photo);

  Optional<UserPermission> findByUserAndMissionGroup(
    User user,
    MissionGroup missionGroup
  );
}
