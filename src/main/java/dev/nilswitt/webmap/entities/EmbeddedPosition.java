package dev.nilswitt.webmap.entities;

import dev.nilswitt.webmap.api.dtos.EmbeddedPositionDto;

import dev.nilswitt.webmap.api.dtos.TacticalIconDto;
import dev.nilswitt.webmap.api.dtos.UnitDto;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Column;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmbeddedPosition implements PositionInterface {

    @Column(name = "embeddedposition_latitude", nullable = true)
    private Double latitude = 0.0;

    @Column(name = "embeddedposition_longitude", nullable = true)
    private Double longitude = 0.0;

    @Column(name = "embeddedposition_altimeter", nullable = true)
    private Double altitude = 0.0;

    @Column(name = "embeddedposition_accuracy", nullable = true)
    private Double accuracy = 0.0;

    @Column(name = "embeddedposition_timestamp",nullable = true)
    private Instant timestamp = null;

    public EmbeddedPosition(Double latitude, Double longitude, Double accuracy, Instant timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.timestamp = timestamp;
    }

    public static EmbeddedPosition of(EmbeddedPositionDto entity) {
        return new EmbeddedPosition(entity.getLatitude(), entity.getLongitude(), entity.getAltitude(), entity.getAccuracy(), entity.getTimestamp());
    }

    public EmbeddedPositionDto toDto() {
        EmbeddedPositionDto dto = new EmbeddedPositionDto();
        dto.setLatitude(latitude);
        dto.setLongitude(longitude);
        dto.setAltitude(altitude);
        dto.setAccuracy(accuracy);
        dto.setTimestamp(timestamp);
        return dto;
    }

    @Override
    public String toString() {
        return "EmbeddedPosition{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                ", accuracy=" + accuracy +
                ", timestamp=" + timestamp +
                '}';
    }
}