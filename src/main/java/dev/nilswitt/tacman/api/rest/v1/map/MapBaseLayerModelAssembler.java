package dev.nilswitt.tacman.api.rest.v1.map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import dev.nilswitt.tacman.api.dtos.MapBaseLayerDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

@Component
public class MapBaseLayerModelAssembler
    implements RepresentationModelAssembler<MapBaseLayerDto, EntityModel<MapBaseLayerDto>>
{

    @Override
    public EntityModel<MapBaseLayerDto> toModel(MapBaseLayerDto entity) {
        return EntityModel.of(
            entity,
            WebMvcLinkBuilder.linkTo(methodOn(MapBaseLayerController.class).one(entity.getId(), null)).withSelfRel(),
            linkTo(methodOn(MapBaseLayerController.class).all(null)).withRel("map/baselayers")
        );
    }
}
