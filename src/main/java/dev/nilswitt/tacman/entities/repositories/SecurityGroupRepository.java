package dev.nilswitt.tacman.entities.repositories;

import dev.nilswitt.tacman.entities.SecurityGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SecurityGroupRepository
        extends JpaRepository<SecurityGroup, UUID> {
    Optional<SecurityGroup> findByName(String name);

    List<SecurityGroup> findBySsoGroupName(String ssoGroupName);

    @Modifying
    @Query(
            value = "DELETE FROM user_security_group WHERE group_id = :groupId",
            nativeQuery = true
    )
    void removeFromAllUsers(@Param("groupId") UUID groupId);

    @Modifying
    @Query(
            value = "DELETE FROM map_overlay_security_group WHERE group_id = :groupId",
            nativeQuery = true
    )
    void removeFromAllOverlays(@Param("groupId") UUID groupId);
}
