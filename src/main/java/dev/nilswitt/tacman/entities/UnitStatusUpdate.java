package dev.nilswitt.tacman.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
public class UnitStatusUpdate extends AbstractEntity {

    @Column(nullable = false)
    private int status = 6;

    @Column(nullable = false)
    private boolean acknowledged = false;

    @ManyToOne(optional = false)
    private Unit unit;

    public UnitStatusUpdate() {}
}
