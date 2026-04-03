package dev.nilswitt.tacman.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Position extends AbstractEntity implements PositionInterface {

    @Column
    private Double latitude = 0.0;

    @Column
    private Double longitude = 0.0;

    @Column
    private Double altitude = 0.0;

    @Column
    private Double accuracy = 0.0;

    @Column
    private Instant timestamp;

    public Position(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
