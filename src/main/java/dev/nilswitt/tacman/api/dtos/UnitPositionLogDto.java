package dev.nilswitt.tacman.api.dtos;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnitPositionLogDto extends AbstractEntityDto {

  private UUID unitId;
  private EmbeddedPositionDto position;

  public UnitPositionLogDto(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String createdBy,
    String modifiedBy,
    UUID unitId,
    EmbeddedPositionDto position
  ) {
    super(id, createdAt, updatedAt, createdBy, modifiedBy);
    this.unitId = unitId;
    this.position = position;
  }
}
