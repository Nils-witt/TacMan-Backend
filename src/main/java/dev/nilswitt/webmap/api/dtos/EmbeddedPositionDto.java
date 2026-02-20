package dev.nilswitt.webmap.api.dtos;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
public class EmbeddedPositionDto {
    private double latitude = 0.0;
    private double longitude = 0.0;
    private double altitude = 0.0;
    private double accuracy = 0.0;

    private Instant timestamp = null;
}
