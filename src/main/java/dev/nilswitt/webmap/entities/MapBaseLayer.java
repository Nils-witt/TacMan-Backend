package dev.nilswitt.webmap.entities;

import dev.nilswitt.webmap.api.dtos.MapBaseLayerDto;
import dev.nilswitt.webmap.entities.eventListeners.EntityEventListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@EntityListeners(EntityEventListener.class)
@Getter
@Setter
public class MapBaseLayer extends AbstractEntity {

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, unique = false, length = 100)
    private String name;

    @Column(nullable = false, unique = false, length = 100)
    private String url;

    @Column
    private String cacheUrl;


    public MapBaseLayerDto toDto() {
        MapBaseLayerDto dto = new MapBaseLayerDto();
        dto.setCreatedAt(getCreatedAt());
        dto.setUpdatedAt(getUpdatedAt());
        dto.setId(getId());
        dto.setName(getName());
        dto.setUrl(getUrl());
        dto.setCacheUrl(getCacheUrl());
        return dto;
    }

    public static MapBaseLayer of(MapBaseLayerDto dto) {
        MapBaseLayer layer = new MapBaseLayer();
        layer.setName(dto.getName());
        layer.setUrl(dto.getUrl());
        layer.setCacheUrl(dto.getCacheUrl());
        return layer;
    }

}