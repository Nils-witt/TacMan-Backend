package dev.nilswitt.tacman.api.dtos;

import dev.nilswitt.tacman.entities.JWTTokenRegistration;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SessionDto extends AbstractEntityDto {

    private UUID tokenId;
    private UUID userId;

    public SessionDto(JWTTokenRegistration registration) {
        super(
            registration.getId(),
            registration.getCreatedAt(),
            registration.getUpdatedAt(),
            registration.getCreatedBy(),
            registration.getModifiedBy()
        );
        this.tokenId = registration.getTokenId();
        this.userId = registration.getUserId();
    }
}
