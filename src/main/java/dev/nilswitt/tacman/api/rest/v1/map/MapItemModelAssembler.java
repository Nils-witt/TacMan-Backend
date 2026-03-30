package dev.nilswitt.tacman.api.rest.v1.map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import dev.nilswitt.tacman.api.dtos.MapItemDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

@Component
public class MapItemModelAssembler implements RepresentationModelAssembler<MapItemDto, EntityModel<MapItemDto>> {

    @Override
    public EntityModel<MapItemDto> toModel(MapItemDto mapItem) {
        return EntityModel.of(
            mapItem,
            WebMvcLinkBuilder.linkTo(methodOn(MapItemController.class).one(mapItem.getId(), null)).withSelfRel(),
            linkTo(methodOn(MapItemController.class).all(null)).withRel("map/items")
        );
    }
}
