package dev.nilswitt.tacman.entities.repositories;

import dev.nilswitt.tacman.entities.MapGroup;
import dev.nilswitt.tacman.entities.MapItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MapItemRepository extends JpaRepository<MapItem, UUID> {
    Optional<MapItem> findByName(String name);

    List<MapItem> findByMapGroup(MapGroup mapGroup);

}

