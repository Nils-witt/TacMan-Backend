package dev.nilswitt.webmap.api.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class PhotoDto extends AbstractEntityDto {

    private String name;
    private EmbeddedPositionDto position;
    private UUID authorId;
    private UUID missionGroupId;

    public PhotoDto(UUID id, Instant createdAt, Instant updatedAt, String name, EmbeddedPositionDto position, UUID authorId, UUID missionGroupId) {
        super(id, createdAt, updatedAt);

        this.name = name;
        this.position = position;
        this.authorId = authorId;
        this.missionGroupId = missionGroupId;
    }

}
