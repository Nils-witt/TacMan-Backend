package dev.nilswitt.tacman.api.rest.v1;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import dev.nilswitt.tacman.api.dtos.SessionDto;
import dev.nilswitt.tacman.entities.User;
import dev.nilswitt.tacman.entities.services.JWTTokenRegistrationService;
import dev.nilswitt.tacman.exceptions.ForbiddenException;
import dev.nilswitt.tacman.security.jwt.JWTRegistry;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/sessions")
public class SessionController {

    private final SessionModelAssembler assembler;
    private final JWTTokenRegistrationService jwtTokenRegistrationService;
    private final JWTRegistry jwtRegistry;

    public SessionController(
        JWTTokenRegistrationService jwtTokenRegistrationService,
        SessionModelAssembler assembler,
        JWTRegistry jwtRegistry
    ) {
        this.jwtTokenRegistrationService = jwtTokenRegistrationService;
        this.assembler = assembler;
        this.jwtRegistry = jwtRegistry;
    }

    @GetMapping("")
    CollectionModel<EntityModel<SessionDto>> all(@AuthenticationPrincipal User userDetails) {
        return CollectionModel.of(
            this.jwtTokenRegistrationService.findByUserId(userDetails.getId())
                .stream()
                .map(SessionDto::new)
                .map(this.assembler::toModel)
                .collect(Collectors.toList()),
            linkTo(methodOn(MissionGroupController.class).all(null)).withSelfRel()
        );
    }

    @DeleteMapping("/{id}")
    void delete(@AuthenticationPrincipal User userDetails, @PathVariable UUID id) {
        var tokenOpt = this.jwtTokenRegistrationService.findByTokenId(id);
        if (tokenOpt.isEmpty()) {
            throw new ForbiddenException("Not Present");
        }
        if (!tokenOpt.get().getUserId().equals(userDetails.getId())) {
            throw new ForbiddenException("User does not have permission to revoke this token.");
        }

        this.jwtTokenRegistrationService.delete(tokenOpt.get());
        this.jwtRegistry.revokeToken(id);
    }
}
