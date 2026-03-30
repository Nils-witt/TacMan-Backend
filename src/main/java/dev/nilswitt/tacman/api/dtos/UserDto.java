package dev.nilswitt.tacman.api.dtos;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserDto extends AbstractEntityDto {

    private String username;
    private String email;
    private String firstName;
    private String lastName;

    private boolean enabled;
    private boolean locked;

    private UUID unitId;

    private Set<UUID> securityGroups;

    public UserDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String createdBy,
        String modifiedBy,
        String username,
        String email,
        String firstName,
        String lastName,
        boolean enabled,
        boolean locked,
        UUID unitId,
        Set<UUID> securityGroups
    ) {
        super(id, createdAt, updatedAt, createdBy, modifiedBy);
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.enabled = enabled;
        this.locked = locked;
        this.unitId = unitId;
        this.securityGroups = securityGroups;
    }
}
