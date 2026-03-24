package dev.nilswitt.tacman.entities.repositories;

import dev.nilswitt.tacman.entities.Unit;
import dev.nilswitt.tacman.entities.UnitStatusUpdate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UnitStatusUpdateRepository
  extends JpaRepository<UnitStatusUpdate, UUID>
{
  List<UnitStatusUpdate> findByUnit(Unit unit);

  UnitStatusUpdate findFirstByUnitOrderByCreatedAtAsc(Unit unit);

  long deleteByUnit(Unit unit);
}
