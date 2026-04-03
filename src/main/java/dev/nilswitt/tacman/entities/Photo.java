package dev.nilswitt.tacman.entities;

import dev.nilswitt.tacman.entities.eventListeners.EntityEventListener;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@EntityListeners(EntityEventListener.class)
@Getter
@Setter
public class Photo extends AbstractEntity {

    @Column
    private String name;

    @Column
    private String path;

    @Embedded
    private EmbeddedPosition position;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(optional = false)
    @JoinColumn(name = "mission_group_id", nullable = false)
    private MissionGroup missionGroup;

    public Photo() {}

    public Photo(String name, String path) {
        this.name = name;
        this.path = path;
    }
}
