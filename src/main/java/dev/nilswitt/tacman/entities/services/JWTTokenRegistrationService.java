package dev.nilswitt.tacman.entities.services;

import dev.nilswitt.tacman.entities.JWTTokenRegistration;
import dev.nilswitt.tacman.entities.repositories.JWTTokenRegistrationRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class JWTTokenRegistrationService {

    private final JWTTokenRegistrationRepository jwtTokenRegistrationRepository;

    public JWTTokenRegistrationService(JWTTokenRegistrationRepository jwtTokenRegistrationRepository) {
        this.jwtTokenRegistrationRepository = jwtTokenRegistrationRepository;
    }

    public List<JWTTokenRegistration> findAll() {
        return jwtTokenRegistrationRepository.findAll();
    }

    public Optional<JWTTokenRegistration> findById(UUID id) {
        return jwtTokenRegistrationRepository.findById(id);
    }

    public JWTTokenRegistration save(JWTTokenRegistration registration) {
        return jwtTokenRegistrationRepository.save(registration);
    }

    public void deleteById(UUID id) {
        jwtTokenRegistrationRepository.deleteById(id);
    }

    public void delete(JWTTokenRegistration registration) {
        jwtTokenRegistrationRepository.delete(registration);
    }

    public Optional<JWTTokenRegistration> findByTokenId(UUID tokenId) {
        return jwtTokenRegistrationRepository.findByTokenId(tokenId);
    }

    public List<JWTTokenRegistration> findByUserId(UUID userId) {
        return jwtTokenRegistrationRepository.findByUserId(userId);
    }

    public void deleteByTokenId(UUID tokenId) {
        jwtTokenRegistrationRepository.deleteByTokenId(tokenId);
    }
}
