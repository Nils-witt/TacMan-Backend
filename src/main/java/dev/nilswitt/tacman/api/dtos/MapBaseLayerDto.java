package dev.nilswitt.tacman.api.dtos;

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
}
