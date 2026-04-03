package dev.nilswitt.tacman.entities;

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
