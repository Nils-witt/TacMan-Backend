package dev.nilswitt.webmap.api.dtos;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class AbstractEntityDto{
    UUID id;
    Instant createdAt;
    Instant updatedAt;

}
