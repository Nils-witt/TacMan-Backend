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
    private double latitude = 0.0;
    @Column
    private double longitude = 0.0;
    @Column
    private double altitude = 0.0;
    @Column
    private double accuracy = 0.0;

    @Column
    private Instant timestamp;
    public Position(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public AbstractEntityDto toDto() {
        AbstractEntityDto dto = new AbstractEntityDto();
        dto.setId(getId());
        return dto;
    }

}