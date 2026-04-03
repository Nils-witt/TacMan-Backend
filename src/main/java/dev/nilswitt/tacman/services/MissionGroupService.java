package dev.nilswitt.tacman.services;

import dev.nilswitt.tacman.api.dtos.MissionGroupDto;
import dev.nilswitt.tacman.entities.MissionGroup;
import dev.nilswitt.tacman.entities.User;
import dev.nilswitt.tacman.entities.repositories.MapGroupRepository;
import dev.nilswitt.tacman.entities.repositories.MissionGroupRepository;
import dev.nilswitt.tacman.entities.repositories.UnitRepository;
import dev.nilswitt.tacman.security.PermissionVerifier;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MissionGroupService {

    private final MissionGroupRepository missionGroupRepository;
    private final PermissionVerifier permissionVerifier;
    private final MapGroupRepository mapGroupRepository;
    private final UnitRepository unitRepository;

    public MissionGroupService(
        MissionGroupRepository missionGroupRepository,
        PermissionVerifier permissionVerifier,
        MapGroupRepository mapGroupRepository,
        UnitRepository unitRepository
    ) {
        this.missionGroupRepository = missionGroupRepository;
        this.permissionVerifier = permissionVerifier;
        this.mapGroupRepository = mapGroupRepository;
        this.unitRepository = unitRepository;
    }

    public List<MissionGroup> findAll() {
        return missionGroupRepository.findAll();
    }

    public Optional<MissionGroup> findById(UUID id) {
        return missionGroupRepository.findById(id);
    }

    public MissionGroup save(MissionGroup missionGroup) {
        this.unitRepository.findAllByMissionGroup(missionGroup).forEach(unit -> {
            if (!missionGroup.getUnits().contains(unit)) {
                unit.setMissionGroup(null);
                this.unitRepository.save(unit);
            }
        });

        missionGroup
            .getUnits()
            .forEach(unit -> {
                unit.setMissionGroup(missionGroup);
                this.unitRepository.save(unit);
            });

        return missionGroupRepository.save(missionGroup);
    }

    public void deleteById(UUID id) {
        missionGroupRepository.deleteById(id);
    }

    public MissionGroupDto toDto(MissionGroup missionGroup, User actingUser) {
        MissionGroupDto dto = new MissionGroupDto(missionGroup);
        dto.setPermissions(this.permissionVerifier.getScopes(missionGroup, actingUser));
        return dto;
    }

    public MissionGroup fromDto(MissionGroupDto dto) {
        MissionGroup missionGroup = new MissionGroup();
        missionGroup.setName(dto.getName());
        missionGroup.setStartTime(dto.getStartTime());
        missionGroup.setEndTime(dto.getEndTime());
        missionGroup.setMapGroups(
            dto.getMapGroupIds() != null
                ? Set.copyOf(mapGroupRepository.findAllById(List.copyOf(dto.getMapGroupIds())))
                : Set.of()
        );
        missionGroup.setUnits(
            dto.getUnitIds() != null
                ? Set.copyOf(this.unitRepository.findAllById(List.copyOf(dto.getUnitIds())))
                : Set.of()
        );
        return missionGroup;
    }
}
