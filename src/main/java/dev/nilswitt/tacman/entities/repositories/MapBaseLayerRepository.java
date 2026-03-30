package dev.nilswitt.tacman.entities.repositories;

import dev.nilswitt.tacman.entities.MapBaseLayer;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MapBaseLayerRepository extends JpaRepository<MapBaseLayer, UUID> {
    Optional<MapBaseLayer> findByName(String name);
}
