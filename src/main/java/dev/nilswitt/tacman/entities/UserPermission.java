package dev.nilswitt.tacman.entities;

import dev.nilswitt.tacman.api.dtos.AbstractEntityDto;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
public class UserPermission extends AbstractPermission {

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @Getter
    @Setter
    private User user;

    public UserPermission() {}

    public AbstractEntityDto toDto() {
        return new AbstractEntityDto(
            this.getId(),
            this.getCreatedAt(),
            this.getUpdatedAt(),
            this.getCreatedBy(),
            this.getModifiedBy()
        );
    }
}
