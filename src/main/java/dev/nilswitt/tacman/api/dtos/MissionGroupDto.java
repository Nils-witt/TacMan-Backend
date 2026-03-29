package dev.nilswitt.tacman.api.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class MissionGroupDto extends AbstractEntityDto {

    private String name;
    private Instant startTime;
    private Instant endTime;
    private Set<UUID> unitIds;
    private Set<UUID> mapGroupIds;
    private EmbeddedPositionDto position;

    public MissionGroupDto(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            String createdBy,
            String modifiedBy,
            String name,
            Instant startTime,
            Instant endTime,
            Set<UUID> unitIds,
            Set<UUID> mapGroupIds,
            EmbeddedPositionDto position
    ) {
        super(id, createdAt, updatedAt, createdBy, modifiedBy);
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.unitIds = unitIds;
        this.mapGroupIds = mapGroupIds;
        this.position = position;
    }
}
