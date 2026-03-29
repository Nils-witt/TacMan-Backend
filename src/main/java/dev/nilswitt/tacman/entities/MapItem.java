package dev.nilswitt.tacman.entities;

import dev.nilswitt.tacman.api.dtos.EmbeddedPositionDto;
import dev.nilswitt.tacman.api.dtos.MapItemDto;
import dev.nilswitt.tacman.entities.eventListeners.EntityEventListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@EntityListeners(EntityEventListener.class)
@Getter
@Setter
public class MapItem extends AbstractEntity {

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, unique = false, length = 100)
    private String name;

    @Embedded
    private EmbeddedPosition position;

    @ManyToOne
    @JoinColumn(name = "map_group_id")
    private MapGroup mapGroup;

    @Column(columnDefinition = "integer default 14")
    private Integer zoomLevel = 14;

    public EmbeddedPosition getPosition() {
        if (position == null) {
            position = new EmbeddedPosition();
        }
        return position;
    }

    public MapItemDto toDto() {
        EmbeddedPositionDto positionDto = new EmbeddedPositionDto();
        positionDto.setLatitude(getPosition().getLatitude());
        positionDto.setLongitude(getPosition().getLongitude());
        positionDto.setAltitude(getPosition().getAltitude());
        positionDto.setTimestamp(getPosition().getTimestamp());

        return new MapItemDto(
                this.getId(),
                this.getCreatedAt(),
                this.getUpdatedAt(),
                this.getCreatedBy(),
                this.getModifiedBy(),
                this.getName(),
                positionDto,
                this.getMapGroup() != null ? this.getMapGroup().getId() : null,
                this.getZoomLevel()
        );
    }

    public static MapItem of(MapItemDto dto) {
        MapItem mapItem = new MapItem();

        mapItem.setName(dto.getName());
        mapItem.setZoomLevel(dto.getZoomLevel());
        if (dto.getPosition() != null) {
            mapItem.setPosition(EmbeddedPosition.of(dto.getPosition()));
        }

        return mapItem;
    }

    @Override
    public String toString() {
        return (
                "MapItem{" +
                        "name='" +
                        name +
                        '\'' +
                        ", position=" +
                        position +
                        ", mapGroup=" +
                        mapGroup +
                        ", zoomLevel=" +
                        zoomLevel +
                        '}'
        );
    }
}
