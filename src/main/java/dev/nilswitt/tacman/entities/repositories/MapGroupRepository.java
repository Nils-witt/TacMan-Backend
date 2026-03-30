package dev.nilswitt.tacman.entities.repositories;

import dev.nilswitt.tacman.entities.MapGroup;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MapGroupRepository extends JpaRepository<MapGroup, UUID> {
    Optional<MapGroup> findByName(String name);
}
