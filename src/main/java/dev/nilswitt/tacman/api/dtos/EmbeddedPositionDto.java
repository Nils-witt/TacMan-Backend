package dev.nilswitt.tacman.api.dtos;

import dev.nilswitt.tacman.entities.EmbeddedPosition;
import java.time.Instant;
import lombok.Data;

@Data
public class EmbeddedPositionDto {

    private Double latitude = 0.0;
    private Double longitude = 0.0;
    private Double altitude = 0.0;
    private Double accuracy = 0.0;

    private Instant timestamp = null;

    public EmbeddedPositionDto() {}

    public EmbeddedPositionDto(EmbeddedPosition position) {
        this.latitude = position.getLatitude();
        this.longitude = position.getLongitude();
        this.altitude = position.getAltitude();
        this.accuracy = position.getAccuracy();
        this.timestamp = position.getTimestamp();
    }
}
