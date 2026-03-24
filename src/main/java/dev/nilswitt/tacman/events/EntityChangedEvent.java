package dev.nilswitt.tacman.events;

import dev.nilswitt.tacman.entities.AbstractEntity;
import java.util.UUID;

public record EntityChangedEvent<T extends AbstractEntity>(
  String className,
  T entity,
  ChangeType changeType,
  UUID id
) {}
