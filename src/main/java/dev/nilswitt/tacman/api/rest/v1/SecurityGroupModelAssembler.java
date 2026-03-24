package dev.nilswitt.tacman.api.rest.v1;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import dev.nilswitt.tacman.api.dtos.SecurityGroupDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

@Component
public class SecurityGroupModelAssembler
  implements
    RepresentationModelAssembler<SecurityGroupDto, EntityModel<SecurityGroupDto>>
{

  @Override
  public EntityModel<SecurityGroupDto> toModel(SecurityGroupDto entity) {
    return EntityModel.of(
      entity,
      WebMvcLinkBuilder.linkTo(
        methodOn(SecurityGroupController.class).one(entity.getId(), null)
      ).withSelfRel(),
      linkTo(
        methodOn(SecurityGroupController.class).all(null)
      ).withRel("securitygroups")
    );
  }
}
