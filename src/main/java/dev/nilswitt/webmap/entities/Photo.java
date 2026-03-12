package dev.nilswitt.webmap.entities;

import dev.nilswitt.webmap.api.dtos.PhotoDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
public class Photo extends AbstractEntity {

    @Column
    private String name;
    @Column
    private String path;

    @Embedded
    private EmbeddedPosition position;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(optional = false)
    @JoinColumn(name = "mission_group_id", nullable = false)
    private MissionGroup missionGroup;

    public Photo() {
    }

    public Photo(String name, String path) {
        this.name = name;
        this.path = path;
    }


    @Override
    public PhotoDto toDto() {
        PhotoDto dto = new PhotoDto();
        dto.setId(this.getId());
        dto.setName(this.getName());
        dto.setPosition(this.getPosition() != null ? this.getPosition().toDto() : null);
        dto.setCreatedAt(this.getCreatedAt());
        dto.setUpdatedAt(this.getUpdatedAt());
        dto.setAuthorId(this.author.getId());
        dto.setMissionGroupId(this.missionGroup.getId());
        return dto;
    }

    public static Photo fromDto(PhotoDto dto) {
        Photo photo = new Photo();
        photo.setName(dto.getName());
        photo.setPosition(dto.getPosition() != null ? EmbeddedPosition.of(dto.getPosition()) : null);
        return photo;
    }
}
