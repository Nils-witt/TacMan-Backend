package dev.nilswitt.tacman.entities;

import dev.nilswitt.tacman.entities.eventListeners.EntityEventListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

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

    @OneToMany(mappedBy = "missionGroup", fetch = FetchType.EAGER)
    private Set<Unit> units = new LinkedHashSet<>();

    @OneToMany(mappedBy = "missionGroup")
    private Set<Photo> photos = new LinkedHashSet<>();

    @Override
    public String toString() {
        return (
            "MissionGroup{" +
            "name='" +
            name +
            '\'' +
            ", startTime=" +
            startTime +
            ", endTime=" +
            endTime +
            ", position=" +
            position +
            ", mapGroups=" +
            mapGroups +
            ", units=" +
            units +
            ", id=" +
            id +
            '}'
        );
    }
}
