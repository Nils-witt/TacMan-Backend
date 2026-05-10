package dev.nilswitt.tacman.entities.services;

import dev.nilswitt.tacman.entities.Unit;
import dev.nilswitt.tacman.entities.UnitStatusUpdate;
import dev.nilswitt.tacman.entities.repositories.UnitStatusUpdateRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UnitStatusUpdateService {

    private final UnitStatusUpdateRepository unitStatusUpdateRepository;

    public UnitStatusUpdateService(UnitStatusUpdateRepository unitStatusUpdateRepository) {
        this.unitStatusUpdateRepository = unitStatusUpdateRepository;
    }

    public List<UnitStatusUpdate> findAll() {
        return unitStatusUpdateRepository.findAll();
    }

    public Optional<UnitStatusUpdate> findById(UUID id) {
        return unitStatusUpdateRepository.findById(id);
    }

    public UnitStatusUpdate save(UnitStatusUpdate statusUpdate) {
        return unitStatusUpdateRepository.save(statusUpdate);
    }

    public void deleteById(UUID id) {
        unitStatusUpdateRepository.deleteById(id);
    }

    public List<UnitStatusUpdate> findByUnit(Unit unit) {
        return unitStatusUpdateRepository.findByUnit(unit);
    }

    public UnitStatusUpdate findFirstByUnitOrderByCreatedAtAsc(Unit unit) {
        return unitStatusUpdateRepository.findFirstByUnitOrderByCreatedAtAsc(unit);
    }

    public long deleteByUnit(Unit unit) {
        return unitStatusUpdateRepository.deleteByUnit(unit);
    }
}
