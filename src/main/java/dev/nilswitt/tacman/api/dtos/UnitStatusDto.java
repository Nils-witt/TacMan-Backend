package dev.nilswitt.tacman.api.dtos;

import dev.nilswitt.tacman.entities.UnitStatusUpdate;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UnitStatusDto extends AbstractEntityDto {

    private int status;
    private boolean acknowledged;
    private UUID unitId;

    public UnitStatusDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String createdBy,
        String modifiedBy,
        int status,
        boolean acknowledged,
        UUID unitId
    ) {
        super(id, createdAt, updatedAt, createdBy, modifiedBy);
        this.status = status;
        this.unitId = unitId;
        this.acknowledged = acknowledged;
    }

    public UnitStatusDto(UnitStatusUpdate statusUpdate) {
        super(
            statusUpdate.getId(),
            statusUpdate.getCreatedAt(),
            statusUpdate.getUpdatedAt(),
            statusUpdate.getCreatedBy(),
            statusUpdate.getModifiedBy()
        );
        this.status = statusUpdate.getStatus();
        this.acknowledged = statusUpdate.isAcknowledged();
        this.unitId = statusUpdate.getUnit().getId();
    }
}
