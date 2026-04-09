package dev.nilswitt.tacman.services;

import dev.nilswitt.tacman.api.dtos.EmbeddedPositionDto;
import dev.nilswitt.tacman.api.dtos.UHSDto;
import dev.nilswitt.tacman.entities.EmbeddedPosition;
import dev.nilswitt.tacman.entities.UHS;
import dev.nilswitt.tacman.entities.User;
import dev.nilswitt.tacman.entities.repositories.MissionGroupRepository;
import dev.nilswitt.tacman.entities.repositories.UHSRepository;
import dev.nilswitt.tacman.entities.repositories.UserRepository;
import dev.nilswitt.tacman.security.PermissionVerifier;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UHSService {

    private final UHSRepository uhsRepository;
    private final PermissionVerifier permissionVerifier;
    private final UserRepository userRepository;
    private final MissionGroupRepository missionGroupRepository;

    public UHSService(
        UHSRepository uhsRepository,
        PermissionVerifier permissionVerifier,
        UserRepository userRepository,
        MissionGroupRepository missionGroupRepository
    ) {
        this.uhsRepository = uhsRepository;
        this.permissionVerifier = permissionVerifier;
        this.userRepository = userRepository;
        this.missionGroupRepository = missionGroupRepository;
    }

    public List<UHS> findAll() {
        return uhsRepository.findAll();
    }

    public Optional<UHS> findById(UUID id) {
        return uhsRepository.findById(id);
    }

    public UHS save(UHS uhs) {
        return uhsRepository.save(uhs);
    }

    public void deleteById(UUID id) {
        uhsRepository.deleteById(id);
    }

    public UHSDto toDto(UHS uhs, User actingUser) {
        UHSDto dto = new UHSDto(uhs);
        dto.setPermissions(this.permissionVerifier.getScopes(uhs, actingUser));
        return dto;
    }

    public UHS fromDto(UHSDto dto) {
        UHS uhs = new UHS();
        uhs.setName(dto.getName());
        uhs.setLocation(dto.getLocation() != null ? EmbeddedPosition.of(dto.getLocation()) : null);
        uhs.setCapacity(dto.getCapacity());
        uhs.setAssignedPersonell(
            dto.getAssignedPersonellIds() != null
                ? new HashSet<>(userRepository.findAllById(dto.getAssignedPersonellIds()))
                : new HashSet<>()
        );
        uhs.setMission(
            dto.getMissionId() != null ? missionGroupRepository.findById(dto.getMissionId()).orElse(null) : null
        );
        return uhs;
    }
}
