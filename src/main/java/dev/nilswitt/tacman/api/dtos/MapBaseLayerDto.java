package dev.nilswitt.tacman.api.dtos;

import dev.nilswitt.tacman.entities.MapBaseLayer;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MapBaseLayerDto extends AbstractEntityDto {

    private String name;
    private String url;
    private String cacheUrl;

    public MapBaseLayerDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String createdBy,
        String modifiedBy,
        String name,
        String url,
        String cacheUrl
    ) {
        super(id, createdAt, updatedAt, createdBy, modifiedBy);
        this.name = name;
        this.url = url;
        this.cacheUrl = cacheUrl;
    }

    public MapBaseLayerDto(MapBaseLayer layer) {
        super(layer.getId(), layer.getCreatedAt(), layer.getUpdatedAt(), layer.getCreatedBy(), layer.getModifiedBy());
        this.name = layer.getName();
        this.url = layer.getUrl();
        this.cacheUrl = layer.getCacheUrl();
    }
}
