package dev.nilswitt.tacman.api.dtos;

import dev.nilswitt.tacman.entities.MapItem;
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
        String createdBy,
        String modifiedBy,
        String name,
        EmbeddedPositionDto position,
        UUID mapGroupId,
        Integer zoomLevel
    ) {
        super(id, createdAt, updatedAt, createdBy, modifiedBy);
        this.name = name;
        this.position = position;
        this.mapGroupId = mapGroupId;
        this.zoomLevel = zoomLevel;
    }

    public MapItemDto(MapItem mapItem) {
        super(
            mapItem.getId(),
            mapItem.getCreatedAt(),
            mapItem.getUpdatedAt(),
            mapItem.getCreatedBy(),
            mapItem.getModifiedBy()
        );
        this.name = mapItem.getName();
        this.position = new EmbeddedPositionDto(mapItem.getPosition());
        this.mapGroupId = mapItem.getMapGroup() != null ? mapItem.getMapGroup().getId() : null;
        this.zoomLevel = mapItem.getZoomLevel();
    }
}
