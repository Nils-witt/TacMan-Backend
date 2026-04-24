package dev.nilswitt.tacman.api.rest.v1;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import dev.nilswitt.tacman.api.dtos.SessionDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

@Component
public class SessionModelAssembler implements RepresentationModelAssembler<SessionDto, EntityModel<SessionDto>> {

    @Override
    public EntityModel<SessionDto> toModel(SessionDto sessionDto) {
        return EntityModel.of(
            sessionDto,
            WebMvcLinkBuilder.linkTo(methodOn(UHSController.class).one(sessionDto.getId(), null)).withSelfRel()
        );
    }
}
