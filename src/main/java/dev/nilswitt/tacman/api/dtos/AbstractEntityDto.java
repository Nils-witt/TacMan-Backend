package dev.nilswitt.tacman.api.dtos;

import dev.nilswitt.tacman.entities.SecurityGroup;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class AbstractEntityDto {
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private Set<SecurityGroup.UserRoleScopeEnum> permissions;

    public AbstractEntityDto(UUID id, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
