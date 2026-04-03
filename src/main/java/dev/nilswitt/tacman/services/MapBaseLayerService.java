package dev.nilswitt.tacman.services;

import dev.nilswitt.tacman.api.dtos.MapBaseLayerDto;
import dev.nilswitt.tacman.entities.MapBaseLayer;
import dev.nilswitt.tacman.entities.User;
import dev.nilswitt.tacman.entities.repositories.MapBaseLayerRepository;
import dev.nilswitt.tacman.security.PermissionVerifier;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MapBaseLayerService {

    private final MapBaseLayerRepository mapBaseLayerRepository;
    private final PermissionVerifier permissionVerifier;

    public MapBaseLayerService(MapBaseLayerRepository mapBaseLayerRepository, PermissionVerifier permissionVerifier) {
        this.mapBaseLayerRepository = mapBaseLayerRepository;
        this.permissionVerifier = permissionVerifier;
    }

    public List<MapBaseLayer> findAll() {
        return mapBaseLayerRepository.findAll();
    }

    public Optional<MapBaseLayer> findById(UUID id) {
        return mapBaseLayerRepository.findById(id);
    }

    public MapBaseLayer save(MapBaseLayer mapBaseLayer) {
        return mapBaseLayerRepository.save(mapBaseLayer);
    }

    public void deleteById(UUID id) {
        mapBaseLayerRepository.deleteById(id);
    }

    public MapBaseLayerDto toDto(MapBaseLayer baseLayer, User actingUser) {
        MapBaseLayerDto dto = new MapBaseLayerDto(baseLayer);
        dto.setPermissions(this.permissionVerifier.getScopes(baseLayer, actingUser));
        return dto;
    }

    public MapBaseLayer fromDto(MapBaseLayerDto dto) {
        MapBaseLayer baseLayer = new MapBaseLayer();
        baseLayer.setName(dto.getName());
        baseLayer.setUrl(dto.getUrl());
        baseLayer.setCacheUrl(dto.getCacheUrl());

        return baseLayer;
    }
}
