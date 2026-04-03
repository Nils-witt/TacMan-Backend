package dev.nilswitt.tacman.api.dtos;

import dev.nilswitt.tacman.entities.SecurityGroup;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SecurityGroupDto extends AbstractEntityDto {

    private String name;
    private String ssoGroupName;
    private List<String> roles;

    public SecurityGroupDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String createdBy,
        String modifiedBy,
        String name,
        List<String> roles,
        String ssoGroupName
    ) {
        super(id, createdAt, updatedAt, createdBy, modifiedBy);
        this.name = name;
        this.roles = roles;
        this.ssoGroupName = ssoGroupName;
    }

    public SecurityGroupDto(SecurityGroup group) {
        super(group.getId(), group.getCreatedAt(), group.getUpdatedAt(), group.getCreatedBy(), group.getModifiedBy());
        this.name = group.getName();
        this.roles = group.getRoles().stream().toList();
        this.ssoGroupName = group.getSsoGroupName();
    }
}
