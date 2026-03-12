package dev.nilswitt.webmap.entities;

import dev.nilswitt.webmap.api.dtos.EmbeddedPositionDto;
import dev.nilswitt.webmap.api.dtos.MissionGroupDto;
import dev.nilswitt.webmap.entities.eventListeners.EntityEventListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@EntityListeners(EntityEventListener.class)
@Getter
@Setter
public class MissionGroup extends AbstractEntity {

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    private Instant startTime;

    @Column
    private Instant endTime;

    @Embedded
    private EmbeddedPosition position;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "mission_group_map_group",
            joinColumns = @JoinColumn(name = "mission_group_id"),
            inverseJoinColumns = @JoinColumn(name = "map_group_id")
    )
    private Set<MapGroup> mapGroups = new LinkedHashSet<>();

    @OneToMany(mappedBy = "missionGroup", fetch =  FetchType.EAGER)
    private Set<Unit> units = new LinkedHashSet<>();

    @OneToMany(mappedBy = "missionGroup")
    private Set<Photo> photos = new LinkedHashSet<>();

    @Override
    public MissionGroupDto toDto() {
        MissionGroupDto dto = new MissionGroupDto();
        dto.setId(getId());
        dto.setCreatedAt(getCreatedAt());
        dto.setUpdatedAt(getUpdatedAt());
        dto.setName(name);
        dto.setStartTime(startTime);
        dto.setEndTime(endTime);
        dto.setUnitIds(units.stream().map(Unit::getId).collect(Collectors.toSet()));
        dto.setMapGroupIds(mapGroups.stream().map(MapGroup::getId).collect(Collectors.toSet()));
        if (this.getPosition() != null) {
            EmbeddedPositionDto positionDto = new EmbeddedPositionDto();
            positionDto.setLatitude(getPosition().getLatitude());
            positionDto.setLongitude(getPosition().getLongitude());
            positionDto.setAltitude(getPosition().getAltitude());
            positionDto.setAccuracy(getPosition().getAccuracy());
            positionDto.setTimestamp(getPosition().getTimestamp());
            dto.setPosition(positionDto);
        }
        return dto;
    }

    public static MissionGroup of(MissionGroupDto dto) {
        MissionGroup mission = new MissionGroup();
        mission.setName(dto.getName());
        mission.setStartTime(dto.getStartTime());
        mission.setEndTime(dto.getEndTime());
        if (dto.getPosition() != null) {
            EmbeddedPosition position = new EmbeddedPosition();
            position.setLatitude(dto.getPosition().getLatitude());
            position.setLongitude(dto.getPosition().getLongitude());
            position.setAltitude(dto.getPosition().getAltitude());
            position.setAccuracy(dto.getPosition().getAccuracy());
            position.setTimestamp(dto.getPosition().getTimestamp());
            mission.setPosition(position);
        }
        return mission;
    }

    @Override
    public String toString() {
        return "MissionGroup{" +
                "name='" + name + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", position=" + position +
                ", mapGroups=" + mapGroups +
                ", units=" + units +
                ", id=" + id +
                '}';
    }
}


