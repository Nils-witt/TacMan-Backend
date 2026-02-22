package dev.nilswitt.webmap.api.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class PhotoDto extends AbstractEntityDto {

    private String name;
    private String path;
    private Instant expiresAt;
    private EmbeddedPositionDto position;

    public PhotoDto() {
    }

    public PhotoDto(UUID id, String name, String path) {
        this.id = id;
        this.name = name;
        this.path = path;
    }

}
