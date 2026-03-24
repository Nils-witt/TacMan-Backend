package dev.nilswitt.tacman.entities.repositories;

import dev.nilswitt.tacman.entities.MissionGroup;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MissionGroupRepository
  extends JpaRepository<MissionGroup, UUID> {}
