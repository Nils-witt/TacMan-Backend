package dev.nilswitt.tacman.api.dtos;

import dev.nilswitt.tacman.entities.Photo;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhotoDto extends AbstractEntityDto {

    private String name;
    private EmbeddedPositionDto position;
    private UUID authorId;
    private UUID missionGroupId;

    public PhotoDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String createdBy,
        String modifiedBy,
        String name,
        EmbeddedPositionDto position,
        UUID authorId,
        UUID missionGroupId
    ) {
        super(id, createdAt, updatedAt, createdBy, modifiedBy);
        this.name = name;
        this.position = position;
        this.authorId = authorId;
        this.missionGroupId = missionGroupId;
    }

    public PhotoDto(Photo photo) {
        super(photo.getId(), photo.getCreatedAt(), photo.getUpdatedAt(), photo.getCreatedBy(), photo.getModifiedBy());
        this.name = photo.getName();
        this.position = photo.getPosition() != null ? new EmbeddedPositionDto(photo.getPosition()) : null;
        this.authorId = photo.getAuthor().getId();
        this.missionGroupId = photo.getMissionGroup() != null ? photo.getMissionGroup().getId() : null;
    }
}
