package dev.nilswitt.tacman.entities.services;

import dev.nilswitt.tacman.entities.PasswordResetToken;
import dev.nilswitt.tacman.entities.User;
import dev.nilswitt.tacman.entities.repositories.PasswordResetTokenRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetTokenService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public PasswordResetTokenService(PasswordResetTokenRepository passwordResetTokenRepository) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    public List<PasswordResetToken> findAll() {
        return passwordResetTokenRepository.findAll();
    }

    public Optional<PasswordResetToken> findById(UUID id) {
        return passwordResetTokenRepository.findById(id);
    }

    public PasswordResetToken save(PasswordResetToken token) {
        return passwordResetTokenRepository.save(token);
    }

    public void deleteById(UUID id) {
        passwordResetTokenRepository.deleteById(id);
    }

    public Optional<PasswordResetToken> findByTokenHash(String tokenHash) {
        return passwordResetTokenRepository.findByTokenHash(tokenHash);
    }

    public void deleteByUser(User user) {
        passwordResetTokenRepository.deleteByUser(user);
    }
}
