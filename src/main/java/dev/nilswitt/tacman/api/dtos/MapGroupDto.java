package dev.nilswitt.tacman.api.dtos;

import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MapGroupDto extends AbstractEntityDto {

  private String name;

  public MapGroupDto(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String name
  ) {
    super(id, createdAt, updatedAt);
    this.name = name;
  }
}
