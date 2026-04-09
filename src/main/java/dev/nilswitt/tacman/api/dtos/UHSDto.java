package dev.nilswitt.tacman.api.dtos;

import dev.nilswitt.tacman.entities.UHS;
import dev.nilswitt.tacman.entities.User;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UHSDto extends AbstractEntityDto {

    private String name;
    private EmbeddedPositionDto location;
    private Integer capacity;
    private Set<UUID> assignedPersonellIds;
    private UUID missionId;

    public UHSDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String createdBy,
        String modifiedBy,
        String name,
        EmbeddedPositionDto location,
        Integer capacity,
        Set<UUID> assignedPersonellIds,
        UUID missionId
    ) {
        super(id, createdAt, updatedAt, createdBy, modifiedBy);
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.assignedPersonellIds = assignedPersonellIds;
        this.missionId = missionId;
    }

    public UHSDto(UHS uhs) {
        super(
            uhs.getId(),
            uhs.getCreatedAt(),
            uhs.getUpdatedAt(),
            uhs.getCreatedBy(),
            uhs.getModifiedBy()
        );
        this.name = uhs.getName();
        this.location = uhs.getLocation() != null ? new EmbeddedPositionDto(uhs.getLocation()) : null;
        this.capacity = uhs.getCapacity();
        this.assignedPersonellIds = uhs.getAssignedPersonell().stream().map(User::getId).collect(Collectors.toSet());
        this.missionId = uhs.getMission() != null ? uhs.getMission().getId() : null;
    }
}
