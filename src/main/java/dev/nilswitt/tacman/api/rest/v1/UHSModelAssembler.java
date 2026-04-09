package dev.nilswitt.tacman.api.rest.v1;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import dev.nilswitt.tacman.api.dtos.UHSDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

@Component
public class UHSModelAssembler implements RepresentationModelAssembler<UHSDto, EntityModel<UHSDto>> {

    @Override
    public EntityModel<UHSDto> toModel(UHSDto uhsDto) {
        return EntityModel.of(
            uhsDto,
            WebMvcLinkBuilder.linkTo(methodOn(UHSController.class).one(uhsDto.getId(), null)).withSelfRel()
        );
    }
}
