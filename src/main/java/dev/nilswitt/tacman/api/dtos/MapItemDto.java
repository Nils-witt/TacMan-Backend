package dev.nilswitt.tacman.api.dtos;

import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MapItemDto extends AbstractEntityDto {

  private String name;
  private EmbeddedPositionDto position;
  private UUID mapGroupId;
  private Integer zoomLevel;

  public MapItemDto(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String name,
    EmbeddedPositionDto position,
    UUID mapGroupId,
    Integer zoomLevel
  ) {
    super(id, createdAt, updatedAt);
    this.name = name;
    this.position = position;
    this.mapGroupId = mapGroupId;
    this.zoomLevel = zoomLevel;
  }
}
