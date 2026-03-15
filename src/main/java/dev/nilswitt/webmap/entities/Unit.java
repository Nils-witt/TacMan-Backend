package dev.nilswitt.webmap.entities;

import dev.nilswitt.webmap.api.dtos.EmbeddedPositionDto;
import dev.nilswitt.webmap.api.dtos.TacticalIconDto;
import dev.nilswitt.webmap.api.dtos.UnitDto;
import dev.nilswitt.webmap.entities.eventListeners.EntityEventListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@EntityListeners(EntityEventListener.class)
@Getter
@Setter
public class Unit extends AbstractEntity {

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100, unique = true)
    private String name;

    @Embedded
    private TacticalIcon icon = new TacticalIcon();

    @Embedded
    private EmbeddedPosition position;

    @Column(nullable = false)
    private int status = 6;

    @Column(nullable = false)
    private boolean speakRequest = false;

    @Column(nullable = false)
    private boolean showOnMap = false;

    @ManyToOne
    @JoinColumn(name = "mission_group_id")
    private MissionGroup missionGroup;

    public EmbeddedPosition getPosition() {
        if (position == null) {
            position = new EmbeddedPosition();
        }
        return position;
    }

    public UnitDto toDto() {
        EmbeddedPositionDto positionDto = new EmbeddedPositionDto();
        positionDto.setLatitude(getPosition().getLatitude());
        positionDto.setLongitude(getPosition().getLongitude());
        positionDto.setAltitude(getPosition().getAltitude());
        positionDto.setAccuracy(getPosition().getAccuracy());
        positionDto.setTimestamp(getPosition().getTimestamp());

        return new UnitDto(
                this.getId(),
                this.getCreatedAt(),
                this.getUpdatedAt(),
                this.getName(),
                getIcon() != null ? getIcon().toDto() : new TacticalIconDto(),
                positionDto,
                this.getStatus(),
                this.isSpeakRequest(),
                this.showOnMap
        );
    }

    public static Unit of(UnitDto dto) {
        Unit unit = new Unit();
        unit.setName(dto.getName());
        unit.setStatus(dto.getStatus());
        unit.setSpeakRequest(dto.isSpeakRequest());
        unit.setShowOnMap(dto.isShowOnMap());
        unit.setIcon(TacticalIcon.fromDto(dto.getIcon()));
        if (dto.getPosition() != null) {
            EmbeddedPosition position = new EmbeddedPosition();
            position.setLatitude(dto.getPosition().getLatitude());
            position.setLongitude(dto.getPosition().getLongitude());
            position.setAltitude(dto.getPosition().getAltitude());
            position.setAccuracy(dto.getPosition().getAccuracy());
            position.setTimestamp(dto.getPosition().getTimestamp());
            unit.setPosition(position);
        }
        return unit;
    }

}