package dev.nilswitt.webmap.api.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class SecurityGroupDto extends AbstractEntityDto {
    private String name;
    private String ssoGroupName;
    private List<String> roles;


    public SecurityGroupDto(UUID id, Instant createdAt, Instant updatedAt, String name, List<String> roles, String ssoGroupName) {
        super(id, createdAt, updatedAt);
        this.name = name;
        this.roles = roles;
        this.ssoGroupName = ssoGroupName;
    }
}
