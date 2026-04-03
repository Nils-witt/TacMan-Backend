package dev.nilswitt.tacman.entities;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
    name = "password_reset_token",
    indexes = {
        @Index(name = "idx_password_reset_token_hash", columnList = "token_hash", unique = true),
        @Index(name = "idx_password_reset_token_user", columnList = "user_id"),
    }
)
public class PasswordResetToken extends AbstractEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    public boolean isExpired() {
        return this.expiresAt != null && this.expiresAt.isBefore(Instant.now());
    }
}
