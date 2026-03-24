package dev.nilswitt.tacman.api.dtos;

import lombok.Data;

import java.time.Instant;

@Data
public class EmbeddedPositionDto {
    private Double latitude = 0.0;
    private Double longitude = 0.0;
    private Double altitude = 0.0;
    private Double accuracy = 0.0;

    private Instant timestamp = null;
}
