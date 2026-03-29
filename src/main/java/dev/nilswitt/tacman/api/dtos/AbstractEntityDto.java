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
    private String createdBy;
    private String modifiedBy;
    private Set<SecurityGroup.UserRoleScopeEnum> permissions;

    public AbstractEntityDto(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            String createdBy,
            String modifiedBy
    ) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.modifiedBy = modifiedBy;
    }
}
