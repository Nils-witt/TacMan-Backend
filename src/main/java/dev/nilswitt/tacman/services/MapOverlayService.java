package dev.nilswitt.tacman.services;

import dev.nilswitt.tacman.api.dtos.MapOverlayDto;
import dev.nilswitt.tacman.entities.MapGroup;
import dev.nilswitt.tacman.entities.MapOverlay;
import dev.nilswitt.tacman.entities.User;
import dev.nilswitt.tacman.entities.repositories.MapGroupRepository;
import dev.nilswitt.tacman.entities.repositories.MapOverlayRepository;
import dev.nilswitt.tacman.security.PermissionVerifier;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MapOverlayService {

    private final MapOverlayRepository mapOverlayRepository;
    private final PermissionVerifier permissionVerifier;
    private final MapGroupRepository mapGroupRepository;

    public MapOverlayService(
        MapOverlayRepository mapOverlayRepository,
        PermissionVerifier permissionVerifier,
        MapGroupRepository mapGroupRepository
    ) {
        this.mapOverlayRepository = mapOverlayRepository;
        this.permissionVerifier = permissionVerifier;
        this.mapGroupRepository = mapGroupRepository;
    }

    public List<MapOverlay> findAll() {
        return mapOverlayRepository.findAll();
    }

    public Optional<MapOverlay> findById(UUID id) {
        return mapOverlayRepository.findById(id);
    }

    public List<MapOverlay> findByMapGroup(MapGroup mapGroup) {
        return mapOverlayRepository.findByMapGroup(mapGroup);
    }

    public MapOverlay save(MapOverlay mapOverlay) {
        return mapOverlayRepository.save(mapOverlay);
    }

    public void deleteById(UUID id) {
        mapOverlayRepository.deleteById(id);
    }

    public MapOverlayDto toDto(MapOverlay mapOverlay, User actingUser) {
        MapOverlayDto dto = new MapOverlayDto(mapOverlay);
        dto.setPermissions(this.permissionVerifier.getScopes(mapOverlay, actingUser));
        return dto;
    }

    public MapOverlay fromDto(MapOverlayDto dto) {
        MapOverlay mapOverlay = new MapOverlay();
        mapOverlay.setName(dto.getName());
        mapOverlay.setBasePath(dto.getBasePath());
        mapOverlay.setLayerVersion(dto.getLayerVersion());
        mapOverlay.setTilePathPattern(dto.getTilePathPattern());
        mapOverlay.setMapGroup(
            dto.getMapGroupId() != null ? mapGroupRepository.findById(dto.getMapGroupId()).orElse(null) : null
        );
        mapOverlay.setBaseUrl(dto.getBaseUrl());

        return mapOverlay;
    }
}
