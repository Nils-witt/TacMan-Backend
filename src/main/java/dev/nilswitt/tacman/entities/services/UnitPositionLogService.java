package dev.nilswitt.tacman.entities.services;

import dev.nilswitt.tacman.entities.Unit;
import dev.nilswitt.tacman.entities.UnitPositionLog;
import dev.nilswitt.tacman.entities.repositories.UnitPositionLogRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UnitPositionLogService {

    private final UnitPositionLogRepository unitPositionLogRepository;

    public UnitPositionLogService(UnitPositionLogRepository unitPositionLogRepository) {
        this.unitPositionLogRepository = unitPositionLogRepository;
    }

    public List<UnitPositionLog> findAll() {
        return unitPositionLogRepository.findAll();
    }

    public Optional<UnitPositionLog> findById(UUID id) {
        return unitPositionLogRepository.findById(id);
    }

    public UnitPositionLog save(UnitPositionLog positionLog) {
        return unitPositionLogRepository.save(positionLog);
    }

    public void deleteById(UUID id) {
        unitPositionLogRepository.deleteById(id);
    }

    public List<UnitPositionLog> findByUnit(Unit unit) {
        return unitPositionLogRepository.findByUnit(unit);
    }

    public List<UnitPositionLog> findByUnitAndPositionTimestampAfter(Unit unit, Instant timestamp) {
        return unitPositionLogRepository.findByUnitAndPosition_TimestampAfter(unit, timestamp);
    }

    public long deleteByUnit(Unit unit) {
        return unitPositionLogRepository.deleteByUnit(unit);
    }

    public long deleteByCreatedAtBefore(Instant cutoff) {
        return unitPositionLogRepository.deleteByCreatedAtBefore(cutoff);
    }
}
