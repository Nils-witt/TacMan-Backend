package dev.nilswitt.tacman.entities;

import dev.nilswitt.tacman.entities.eventListeners.EntityEventListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@EntityListeners(EntityEventListener.class)
@Getter
@Setter
public class MapGroup extends AbstractEntity {

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @OneToMany(mappedBy = "mapGroup", orphanRemoval = true)
    private Set<MapItem> mapItems = new LinkedHashSet<>();

    @OneToMany(mappedBy = "mapGroup", orphanRemoval = true)
    private Set<MapOverlay> mapOverlays = new LinkedHashSet<>();
}
