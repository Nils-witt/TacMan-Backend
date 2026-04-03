package dev.nilswitt.tacman.services;

import dev.nilswitt.tacman.api.dtos.MapItemDto;
import dev.nilswitt.tacman.entities.EmbeddedPosition;
import dev.nilswitt.tacman.entities.MapGroup;
import dev.nilswitt.tacman.entities.MapItem;
import dev.nilswitt.tacman.entities.User;
import dev.nilswitt.tacman.entities.repositories.MapGroupRepository;
import dev.nilswitt.tacman.entities.repositories.MapItemRepository;
import dev.nilswitt.tacman.security.PermissionVerifier;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MapItemService {

    private final MapItemRepository mapItemRepository;
    private final PermissionVerifier permissionVerifier;
    private final MapGroupRepository mapGroupRepository;

    public MapItemService(
        MapItemRepository mapItemRepository,
        PermissionVerifier permissionVerifier,
        MapGroupRepository mapGroupRepository
    ) {
        this.mapItemRepository = mapItemRepository;
        this.permissionVerifier = permissionVerifier;
        this.mapGroupRepository = mapGroupRepository;
    }

    public List<MapItem> findAll() {
        return mapItemRepository.findAll();
    }

    public Optional<MapItem> findById(UUID id) {
        return mapItemRepository.findById(id);
    }

    public List<MapItem> findByMapGroup(MapGroup mapGroup) {
        return mapItemRepository.findByMapGroup(mapGroup);
    }

    public MapItem save(MapItem mapItem) {
        return mapItemRepository.save(mapItem);
    }

    public void deleteById(UUID id) {
        mapItemRepository.deleteById(id);
    }

    public MapItemDto toDto(MapItem mapItem, User actingUser) {
        MapItemDto dto = new MapItemDto(mapItem);
        dto.setPermissions(this.permissionVerifier.getScopes(mapItem, actingUser));
        return dto;
    }

    public MapItem fromDto(MapItemDto dto) {
        MapItem mapItem = new MapItem();
        mapItem.setName(dto.getName());
        mapItem.setPosition(EmbeddedPosition.of(dto.getPosition()));
        mapItem.setZoomLevel(dto.getZoomLevel());
        mapItem.setMapGroup(
            dto.getMapGroupId() != null ? mapGroupRepository.findById(dto.getMapGroupId()).orElse(null) : null
        );

        return mapItem;
    }
}
