package dev.nilswitt.tacman.api.dtos;

import com.fasterxml.jackson.annotation.JsonGetter;
import dev.nilswitt.tacman.entities.MapOverlay;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MapOverlayDto extends AbstractEntityDto {

    private String name;
    private String baseUrl;
    private String basePath = "";
    private String tilePathPattern;
    private int layerVersion;
    private UUID mapGroupId;

    public MapOverlayDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String createdBy,
        String modifiedBy,
        String name,
        String baseUrl,
        String basePath,
        String tilePathPattern,
        int layerVersion,
        UUID mapGroupId
    ) {
        super(id, createdAt, updatedAt, createdBy, modifiedBy);
        this.name = name;
        this.baseUrl = baseUrl;
        this.basePath = basePath;
        this.tilePathPattern = tilePathPattern;
        this.layerVersion = layerVersion;
        this.mapGroupId = mapGroupId;
    }

    public MapOverlayDto(MapOverlay overlay) {
        super(
            overlay.getId(),
            overlay.getCreatedAt(),
            overlay.getUpdatedAt(),
            overlay.getCreatedBy(),
            overlay.getModifiedBy()
        );
        this.name = overlay.getName();
        this.baseUrl = overlay.getBaseUrl();
        this.basePath = overlay.getBasePath();
        this.tilePathPattern = overlay.getTilePathPattern();
        this.layerVersion = overlay.getLayerVersion();
        this.mapGroupId = overlay.getMapGroup() != null ? overlay.getMapGroup().getId() : null;
    }

    @JsonGetter("fullTileUrl")
    public String getFullTileUrl() {
        return (
            this.baseUrl +
            "/" +
            this.basePath +
            "/" +
            this.getId() +
            "/" +
            this.layerVersion +
            "/" +
            this.tilePathPattern
        );
    }
}
