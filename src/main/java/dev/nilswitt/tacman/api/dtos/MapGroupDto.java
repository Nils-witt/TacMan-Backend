package dev.nilswitt.tacman.api.dtos;

import dev.nilswitt.tacman.entities.MapGroup;
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
        String createdBy,
        String modifiedBy,
        String name
    ) {
        super(id, createdAt, updatedAt, createdBy, modifiedBy);
        this.name = name;
    }

    public MapGroupDto(MapGroup mapGroup) {
        super(
            mapGroup.getId(),
            mapGroup.getCreatedAt(),
            mapGroup.getUpdatedAt(),
            mapGroup.getCreatedBy(),
            mapGroup.getModifiedBy()
        );
        this.name = mapGroup.getName();
    }
}
