package dev.nilswitt.tacman.entities;

import dev.nilswitt.tacman.entities.eventListeners.EntityEventListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@EntityListeners(EntityEventListener.class)
@Getter
@Setter
public class UHS extends AbstractEntity {

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @Embedded
    private EmbeddedPosition location;

    @Column
    private Integer capacity;

    @ManyToMany
    @JoinTable(
        name = "uhs_assigned_personell",
        joinColumns = @JoinColumn(name = "uhs_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> assignedPersonell = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "mission_id")
    private MissionGroup mission;
}
