package dev.nilswitt.tacman.entities.repositories;

import dev.nilswitt.tacman.entities.MissionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MissionGroupRepository
        extends JpaRepository<MissionGroup, UUID> {
}
