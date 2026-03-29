package dev.nilswitt.tacman.api.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class UnitDto extends AbstractEntityDto {

    private String name;
    private TacticalIconDto icon;
    private EmbeddedPositionDto position;
    private int status;
    private boolean speakRequest = false;

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
            int status
    ) {
        super(id, createdAt, updatedAt, createdBy, modifiedBy);
        this.name = name;
        this.icon = icon;
        this.position = position;
        this.speakRequest = speakRequest;
        this.status = status;
    }
}
