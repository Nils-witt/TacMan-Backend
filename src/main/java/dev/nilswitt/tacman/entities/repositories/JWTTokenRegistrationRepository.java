package dev.nilswitt.tacman.entities.repositories;

import dev.nilswitt.tacman.entities.JWTTokenRegistration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JWTTokenRegistrationRepository extends JpaRepository<JWTTokenRegistration, UUID> {
    Optional<JWTTokenRegistration> findByTokenId(UUID tokenId);
    List<JWTTokenRegistration> findByUserId(UUID userId);

    void deleteByTokenId(UUID tokenId);
}
