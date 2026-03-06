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
    @Column(name = "expires_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Instant expiresAt;

    @Embedded
    private EmbeddedPosition position;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

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
        dto.setPath(this.getPath());
        dto.setExpiresAt(this.getExpiresAt());
        dto.setPosition(this.getPosition() != null ? this.getPosition().toDto() : null);
        dto.setCreatedAt(this.getCreatedAt());
        dto.setUpdatedAt(this.getUpdatedAt());

        return dto;
    }

    public static Photo fromDto(PhotoDto dto) {
        Photo photo = new Photo();
        photo.setName(dto.getName());
        photo.setPath(dto.getPath());
        photo.setExpiresAt(dto.getExpiresAt());
        photo.setPosition(dto.getPosition() != null ? EmbeddedPosition.of(dto.getPosition()) : null);
        return photo;
    }
}
