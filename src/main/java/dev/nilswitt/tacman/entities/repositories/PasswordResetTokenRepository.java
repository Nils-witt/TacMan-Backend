package dev.nilswitt.tacman.entities.repositories;

import dev.nilswitt.tacman.entities.PasswordResetToken;
import dev.nilswitt.tacman.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    void deleteByUser(User user);
}
