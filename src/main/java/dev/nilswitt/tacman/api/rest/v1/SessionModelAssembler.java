package dev.nilswitt.tacman.api.rest.v1;

import dev.nilswitt.tacman.api.dtos.SessionDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class SessionModelAssembler implements RepresentationModelAssembler<SessionDto, EntityModel<SessionDto>> {

    @Override
    public EntityModel<SessionDto> toModel(SessionDto sessionDto) {
        return EntityModel.of(
            sessionDto,
            WebMvcLinkBuilder.linkTo(methodOn(SessionController.class).all(null)).withSelfRel()
        );
    }
}
