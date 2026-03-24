package dev.nilswitt.tacman.api.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class MapBaseLayerDto extends AbstractEntityDto {

    private String name;
    private String url;
    private String cacheUrl;

    public MapBaseLayerDto(UUID id, Instant createdAt, Instant updatedAt, String name, String url, String cacheUrl) {
        super(id, createdAt, updatedAt);
        this.name = name;
        this.url = url;
        this.cacheUrl = cacheUrl;

    }
}
