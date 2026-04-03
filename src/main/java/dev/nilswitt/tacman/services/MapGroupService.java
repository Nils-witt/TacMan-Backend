package dev.nilswitt.tacman.services;

import dev.nilswitt.tacman.api.dtos.MapGroupDto;
import dev.nilswitt.tacman.entities.MapGroup;
import dev.nilswitt.tacman.entities.User;
import dev.nilswitt.tacman.entities.repositories.MapGroupRepository;
import dev.nilswitt.tacman.security.PermissionVerifier;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MapGroupService {

    private final MapGroupRepository mapGroupRepository;
    private final PermissionVerifier permissionVerifier;

    public MapGroupService(MapGroupRepository mapGroupRepository, PermissionVerifier permissionVerifier) {
        this.mapGroupRepository = mapGroupRepository;
        this.permissionVerifier = permissionVerifier;
    }

    public List<MapGroup> findAll() {
        return mapGroupRepository.findAll();
    }

    public List<MapGroup> findAllById(Collection<UUID> ids) {
        return mapGroupRepository.findAllById(ids);
    }

    public Optional<MapGroup> findById(UUID id) {
        return mapGroupRepository.findById(id);
    }

    public MapGroup save(MapGroup mapGroup) {
        return mapGroupRepository.save(mapGroup);
    }

    public void deleteById(UUID id) {
        mapGroupRepository.deleteById(id);
    }

    public MapGroupDto toDto(MapGroup mapGroup, User actingUser) {
        MapGroupDto dto = new MapGroupDto(mapGroup);
        dto.setPermissions(this.permissionVerifier.getScopes(mapGroup, actingUser));
        return dto;
    }

    public MapGroup fromDto(MapGroupDto dto) {
        MapGroup mapGroup = new MapGroup();
        mapGroup.setName(dto.getName());

        return mapGroup;
    }
}
