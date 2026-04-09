package dev.nilswitt.tacman.entities.repositories;

import dev.nilswitt.tacman.entities.UHS;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UHSRepository extends JpaRepository<UHS, UUID> {}
