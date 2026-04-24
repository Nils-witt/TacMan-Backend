package dev.nilswitt.tacman.security.jwt;

import dev.nilswitt.tacman.entities.JWTTokenRegistration;
import dev.nilswitt.tacman.entities.repositories.JWTTokenRegistrationRepository;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JWTRegistry {

    private final ConcurrentHashMap<UUID, Boolean> validTokens = new ConcurrentHashMap<>();
    private final JWTTokenRegistrationRepository repository;

    public JWTRegistry(JWTTokenRegistrationRepository repository) {
        this.repository = repository;
    }

    public boolean isValid(UUID tokenId) {
        if (validTokens.get(tokenId) != null) {
            return validTokens.get(tokenId);
        }

        if (repository.findByTokenId(tokenId).isPresent()) {
            validTokens.put(tokenId, true);
            return true;
        }
        return false;
    }

    public void addToken(JWTTokenRegistration token) {
        this.repository.save(token);
        this.validTokens.put(token.getTokenId(), true);
    }

    public void revokeToken(UUID tokenId) {
        validTokens.remove(tokenId);
    }
}
