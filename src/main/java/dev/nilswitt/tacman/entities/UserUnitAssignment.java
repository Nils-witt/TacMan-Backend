package dev.nilswitt.tacman.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserUnitAssignment extends AbstractEntity {

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private Unit unit;

    @Column(nullable = false)
    private Instant startTime;

    @Column
    private Instant endTime;
}
