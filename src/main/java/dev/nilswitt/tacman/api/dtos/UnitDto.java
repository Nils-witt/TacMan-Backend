package dev.nilswitt.tacman.api.dtos;

import dev.nilswitt.tacman.entities.Unit;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UnitDto extends AbstractEntityDto {

    private String name;
    private TacticalIconDto icon;
    private EmbeddedPositionDto position;
    private int status;
    private boolean speakRequest = false;
    private UUID missionGroupId;

    public UnitDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String createdBy,
        String modifiedBy,
        String name,
        TacticalIconDto icon,
        EmbeddedPositionDto position,
        boolean speakRequest,
        int status,
        UUID missionGroupId
    ) {
        super(id, createdAt, updatedAt, createdBy, modifiedBy);
        this.name = name;
        this.icon = icon;
        this.position = position;
        this.speakRequest = speakRequest;
        this.status = status;
        this.missionGroupId = missionGroupId;
    }

    public UnitDto(Unit unit) {
        super(unit.getId(), unit.getCreatedAt(), unit.getUpdatedAt(), unit.getCreatedBy(), unit.getModifiedBy());
        this.name = unit.getName();
        this.icon = unit.getIcon() != null ? new TacticalIconDto(unit.getIcon()) : null;
        this.position = unit.getPosition() != null ? new EmbeddedPositionDto(unit.getPosition()) : null;
        this.speakRequest = unit.isSpeakRequest();
        this.status = unit.getStatus();
        this.missionGroupId = unit.getMissionGroup() != null ? unit.getMissionGroup().getId() : null;
    }
}
