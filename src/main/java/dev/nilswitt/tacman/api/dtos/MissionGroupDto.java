package dev.nilswitt.tacman.api.dtos;

import dev.nilswitt.tacman.entities.MapGroup;
import dev.nilswitt.tacman.entities.MissionGroup;
import dev.nilswitt.tacman.entities.Unit;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.EqualsAndHashCode;

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

    public MissionGroupDto(MissionGroup missionGroup) {
        super(
            missionGroup.getId(),
            missionGroup.getCreatedAt(),
            missionGroup.getUpdatedAt(),
            missionGroup.getCreatedBy(),
            missionGroup.getModifiedBy()
        );
        this.name = missionGroup.getName();
        this.startTime = missionGroup.getStartTime();
        this.endTime = missionGroup.getEndTime();
        this.unitIds = missionGroup.getUnits().stream().map(Unit::getId).collect(Collectors.toSet());
        this.mapGroupIds = missionGroup.getMapGroups().stream().map(MapGroup::getId).collect(Collectors.toSet());
        this.position = missionGroup.getPosition() != null ? new EmbeddedPositionDto(missionGroup.getPosition()) : null;
    }
}
