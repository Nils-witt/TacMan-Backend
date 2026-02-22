package dev.nilswitt.webmap.api.controller;

import dev.nilswitt.webmap.api.dtos.PhotoDto;
import dev.nilswitt.webmap.api.dtos.UnitDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class PhotoModelAssembler implements RepresentationModelAssembler<PhotoDto, EntityModel<PhotoDto>> {

    @Override
    public EntityModel<PhotoDto> toModel(PhotoDto photo) {

        return EntityModel.of(photo,
                WebMvcLinkBuilder.linkTo(methodOn(UnitController.class).one(photo.getId(), null)).withSelfRel());
    }
}
