package dev.nilswitt.tacman.entities.repositories;

import dev.nilswitt.tacman.entities.MapGroup;
import dev.nilswitt.tacman.entities.MapOverlay;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MapOverlayRepository extends JpaRepository<MapOverlay, UUID> {
    Optional<MapOverlay> findByName(String name);

    List<MapOverlay> findByMapGroup(MapGroup mapGroup);
}
