package dev.nilswitt.tacman.entities.repositories;

import dev.nilswitt.tacman.entities.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityGroupPermissionsRepository
  extends JpaRepository<SecurityGroupPermission, UUID>
{
  List<SecurityGroupPermission> findByMapOverlay(MapOverlay mapOverlay);

  List<SecurityGroupPermission> findByMapItem(MapItem mapItem);

  List<SecurityGroupPermission> findByMapGroup(MapGroup mapGroup);

  List<SecurityGroupPermission> findByBaseLayer(MapBaseLayer mapBaseLayer);

  List<SecurityGroupPermission> findByUnit(Unit unit);

  List<SecurityGroupPermission> findByEntityUser(User entityUser);

  List<SecurityGroupPermission> findByPhoto(Photo photo);

  List<SecurityGroupPermission> findByMissionGroup(MissionGroup missionGroup);

  void deleteBySecurityGroup(SecurityGroup securityGroup);

  List<SecurityGroupPermission> findBySecurityGroupAndMapOverlayNotNull(
    SecurityGroup securityGroup
  );

  List<SecurityGroupPermission> findBySecurityGroupAndMapItemNotNull(
    SecurityGroup securityGroup
  );

  List<SecurityGroupPermission> findBySecurityGroupAndMapGroupNotNull(
    SecurityGroup securityGroup
  );

  List<SecurityGroupPermission> findBySecurityGroupAndBaseLayerNotNull(
    SecurityGroup securityGroup
  );

  List<SecurityGroupPermission> findBySecurityGroupAndUnitNotNull(
    SecurityGroup securityGroup
  );

  List<SecurityGroupPermission> findBySecurityGroupAndEntityUserNotNull(
    SecurityGroup securityGroup
  );

  List<SecurityGroupPermission> findBySecurityGroupAndPhotoNotNull(
    SecurityGroup securityGroup
  );

  List<SecurityGroupPermission> findBySecurityGroupAndMissionGroupNotNull(
    SecurityGroup securityGroup
  );

  Optional<SecurityGroupPermission> findBySecurityGroupAndMapItem(
    SecurityGroup securityGroup,
    MapItem mapItem
  );

  Optional<SecurityGroupPermission> findBySecurityGroupAndMapGroup(
    SecurityGroup securityGroup,
    MapGroup mapGroup
  );

  Optional<SecurityGroupPermission> findBySecurityGroupAndMapOverlay(
    SecurityGroup securityGroup,
    MapOverlay mapOverlay
  );

  Optional<SecurityGroupPermission> findBySecurityGroupAndBaseLayer(
    SecurityGroup securityGroup,
    MapBaseLayer mapBaseLayer
  );

  Optional<SecurityGroupPermission> findBySecurityGroupAndUnit(
    SecurityGroup securityGroup,
    Unit unit
  );

  Optional<SecurityGroupPermission> findBySecurityGroupAndEntityUser(
    SecurityGroup securityGroup,
    User entityUser
  );

  Optional<SecurityGroupPermission> findBySecurityGroupAndPhoto(
    SecurityGroup securityGroup,
    Photo photo
  );

  Optional<SecurityGroupPermission> findBySecurityGroupAndMissionGroup(
    SecurityGroup securityGroup,
    MissionGroup missionGroup
  );
}
