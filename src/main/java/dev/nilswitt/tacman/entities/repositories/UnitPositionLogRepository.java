package dev.nilswitt.tacman.entities.repositories;

import dev.nilswitt.tacman.entities.Unit;
import dev.nilswitt.tacman.entities.UnitPositionLog;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnitPositionLogRepository
  extends JpaRepository<UnitPositionLog, UUID>
{
  List<UnitPositionLog> findByUnit(Unit unit);

  List<UnitPositionLog> findByUnitAndPosition_TimestampAfter(
    Unit unit,
    Instant timestamp
  );

  long deleteByUnit(Unit unit);
}
