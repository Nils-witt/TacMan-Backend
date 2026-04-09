package dev.nilswitt.tacman.api.rest.v1;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import dev.nilswitt.tacman.api.dtos.PatientDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

@Component
public class PatientModelAssembler implements RepresentationModelAssembler<PatientDto, EntityModel<PatientDto>> {

    @Override
    public EntityModel<PatientDto> toModel(PatientDto patientDto) {
        return EntityModel.of(
            patientDto,
            WebMvcLinkBuilder.linkTo(methodOn(PatientController.class).one(patientDto.getId(), null)).withSelfRel()
        );
    }
}
