package dev.nilswitt.webmap.api.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserDto extends AbstractEntityDto {
    private String username;
    private String email;
    private String firstName;
    private String lastName;

    private UUID unitId;

    public UserDto(UUID id, Instant createdAt, Instant updatedAt, String username, String email, String firstName, String lastName, UUID unitId) {
        super(id, createdAt, updatedAt);
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.unitId = unitId;
    }
}
