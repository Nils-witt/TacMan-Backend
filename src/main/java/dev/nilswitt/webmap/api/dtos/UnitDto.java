package dev.nilswitt.webmap.api.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class UnitDto extends AbstractEntityDto {

    private String name;
    private TacticalIconDto icon = null;
    private EmbeddedPositionDto position;
    private int status = 6;
    private boolean speakRequest = false;
    private boolean showOnMap = false;

    public UnitDto(UUID id, Instant createdAt, Instant updatedAt, String name, TacticalIconDto icon, EmbeddedPositionDto position, int status, boolean speakRequest, boolean showOnMap) {
        super(id, createdAt, updatedAt);
        this.name = name;
        this.icon = icon;
        this.position = position;
        this.status = status;
        this.speakRequest = speakRequest;
        this.showOnMap = showOnMap;
    }
}
