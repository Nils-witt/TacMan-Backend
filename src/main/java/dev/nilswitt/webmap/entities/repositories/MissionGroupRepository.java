package dev.nilswitt.webmap.entities.repositories;

import dev.nilswitt.webmap.entities.MissionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MissionGroupRepository extends JpaRepository<MissionGroup, UUID> {
}
