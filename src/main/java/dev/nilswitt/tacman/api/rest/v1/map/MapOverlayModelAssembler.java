package dev.nilswitt.tacman.api.rest.v1.map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import dev.nilswitt.tacman.api.dtos.MapOverlayDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

@Component
public class MapOverlayModelAssembler
    implements RepresentationModelAssembler<MapOverlayDto, EntityModel<MapOverlayDto>>
{

    @Override
    public EntityModel<MapOverlayDto> toModel(MapOverlayDto mapItem) {
        return EntityModel.of(
            mapItem,
            WebMvcLinkBuilder.linkTo(methodOn(MapOverlayController.class).one(mapItem.getId(), null)).withSelfRel(),
            linkTo(methodOn(MapOverlayController.class).all(null)).withRel("map/items")
        );
    }
}
