package dev.nilswitt.tacman.api.dtos;

import dev.nilswitt.tacman.entities.SecurityGroup;
import dev.nilswitt.tacman.entities.User;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
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

    public UserDto(User user) {
        super(user.getId(), user.getCreatedAt(), user.getUpdatedAt(), user.getCreatedBy(), user.getModifiedBy());
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.enabled = user.isEnabled();
        this.locked = user.isLocked();
        this.unitId = user.getUnit() != null ? user.getUnit().getId() : null;
        this.securityGroups = user.getSecurityGroups().stream().map(SecurityGroup::getId).collect(Collectors.toSet());
    }
}
