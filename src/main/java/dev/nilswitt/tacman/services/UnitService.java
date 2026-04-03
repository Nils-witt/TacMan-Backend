package dev.nilswitt.tacman.services;

import dev.nilswitt.tacman.api.dtos.UnitDto;
import dev.nilswitt.tacman.entities.*;
import dev.nilswitt.tacman.entities.repositories.UnitPositionLogRepository;
import dev.nilswitt.tacman.entities.repositories.UnitRepository;
import dev.nilswitt.tacman.entities.repositories.UnitStatusUpdateRepository;
import dev.nilswitt.tacman.security.PermissionVerifier;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UnitService {

    private final UnitRepository unitRepository;
    private final UnitStatusUpdateRepository unitStatusUpdateRepository;
    private final UnitPositionLogRepository unitPositionLogRepository;
    private final PermissionVerifier permissionVerifier;
    private final MissionGroupService missionGroupService;

    public UnitService(
        UnitRepository unitRepository,
        UnitStatusUpdateRepository unitStatusUpdateRepository,
        UnitPositionLogRepository unitPositionLogRepository,
        PermissionVerifier permissionVerifier,
        MissionGroupService missionGroupService
    ) {
        this.unitRepository = unitRepository;
        this.unitStatusUpdateRepository = unitStatusUpdateRepository;
        this.unitPositionLogRepository = unitPositionLogRepository;
        this.permissionVerifier = permissionVerifier;
        this.missionGroupService = missionGroupService;
    }

    public List<Unit> findAll() {
        return unitRepository.findAll();
    }

    public Optional<Unit> findById(UUID id) {
        return unitRepository.findById(id);
    }

    public List<Unit> findAllById(Collection<UUID> ids) {
        return unitRepository.findAllById(ids);
    }

    public Optional<Unit> findByName(String name) {
        return unitRepository.findByName(name);
    }

    public List<Unit> findAllByMissionGroup(MissionGroup missionGroup) {
        return unitRepository.findAllByMissionGroup(missionGroup);
    }

    public Unit save(Unit unit) {
        return unitRepository.save(unit);
    }

    public void delete(Unit unit) {
        deletePositionLogsByUnit(unit);
        deleteStatusUpdatesByUnit(unit);
        unitRepository.deleteById(unit.getId());
    }

    public List<UnitStatusUpdate> findStatusUpdatesByUnit(Unit unit) {
        return unitStatusUpdateRepository.findByUnit(unit);
    }

    public UnitStatusUpdate saveStatusUpdate(UnitStatusUpdate statusUpdate) {
        return unitStatusUpdateRepository.save(statusUpdate);
    }

    public void deleteStatusUpdatesByUnit(Unit unit) {
        unitStatusUpdateRepository.deleteByUnit(unit);
    }

    public List<UnitPositionLog> findPositionLogsByUnit(Unit unit) {
        return unitPositionLogRepository.findByUnit(unit);
    }

    public List<UnitPositionLog> findPositionLogsByUnitAfter(Unit unit, Instant timestamp) {
        return unitPositionLogRepository.findByUnitAndPosition_TimestampAfter(unit, timestamp);
    }

    public UnitPositionLog savePositionLog(UnitPositionLog positionLog) {
        return unitPositionLogRepository.save(positionLog);
    }

    public void deletePositionLogsByUnit(Unit unit) {
        unitPositionLogRepository.deleteByUnit(unit);
    }

    public UnitDto toDto(Unit unit, User actingUser) {
        UnitDto dto = new UnitDto(unit);
        dto.setPermissions(this.permissionVerifier.getScopes(unit, actingUser));
        return dto;
    }

    public Unit fromDto(UnitDto dto) {
        Unit unit = new Unit();
        unit.setName(dto.getName());
        unit.setMissionGroup(
            dto.getMissionGroupId() != null ? missionGroupService.findById(dto.getMissionGroupId()).orElse(null) : null
        );
        unit.setIcon(TacticalIcon.of(dto.getIcon()));
        unit.setStatus(dto.getStatus());
        unit.setPosition(dto.getPosition() != null ? EmbeddedPosition.of(dto.getPosition()) : null);
        unit.setSpeakRequest(dto.isSpeakRequest());

        return unit;
    }
}
