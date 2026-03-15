package dev.nilswitt.webmap.entities;

import dev.nilswitt.webmap.api.dtos.AbstractEntityDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

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


    public AbstractEntityDto toDto() {
        return new AbstractEntityDto(this.getId(), this.getCreatedAt(), this.getUpdatedAt());
    }

}