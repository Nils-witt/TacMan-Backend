package dev.nilswitt.tacman.api.rest.v1;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import dev.nilswitt.tacman.api.dtos.PatientDto;
import dev.nilswitt.tacman.entities.Patient;
import dev.nilswitt.tacman.entities.SecurityGroup;
import dev.nilswitt.tacman.entities.User;
import dev.nilswitt.tacman.entities.repositories.UserRepository;
import dev.nilswitt.tacman.exceptions.ForbiddenException;
import dev.nilswitt.tacman.exceptions.PatientNotFoundException;
import dev.nilswitt.tacman.security.PermissionVerifier;
import dev.nilswitt.tacman.services.PatientService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/patients")
public class PatientController {

    private final PatientService patientService;
    private final PatientModelAssembler assembler;
    private final PermissionVerifier permissionVerifier;
    private final UserRepository userRepository;

    public PatientController(
        PatientService patientService,
        PatientModelAssembler assembler,
        PermissionVerifier permissionVerifier,
        UserRepository userRepository
    ) {
        this.patientService = patientService;
        this.assembler = assembler;
        this.permissionVerifier = permissionVerifier;
        this.userRepository = userRepository;
    }

    @GetMapping("")
    CollectionModel<EntityModel<PatientDto>> all(@AuthenticationPrincipal User userDetails) {
        if (
            !this.permissionVerifier.hasAccess(
                userDetails,
                SecurityGroup.UserRoleScopeEnum.VIEW,
                SecurityGroup.UserRoleTypeEnum.PATIENT
            )
        ) {
            throw new ForbiddenException("User does not have permission to view patients.");
        }
        List<EntityModel<PatientDto>> entities = this.patientService.findAll()
            .stream()
            .map(patient -> this.patientService.toDto(patient, userDetails))
            .map(this.assembler::toModel)
            .collect(Collectors.toList());
        return CollectionModel.of(entities, linkTo(methodOn(PatientController.class).all(null)).withSelfRel());
    }

    @PostMapping("")
    EntityModel<PatientDto> newEntity(
        @RequestBody PatientCreatePayload newEntity,
        @AuthenticationPrincipal User userDetails
    ) {
        if (
            !this.permissionVerifier.hasAccess(
                userDetails,
                SecurityGroup.UserRoleScopeEnum.CREATE,
                SecurityGroup.UserRoleTypeEnum.PATIENT
            )
        ) {
            throw new ForbiddenException("User does not have permission to create patients.");
        }

        Patient entity = new Patient();
        entity.setFirstName(newEntity.firstName());
        entity.setLastName(newEntity.lastName());
        entity.setBirthdate(newEntity.birthdate());
        entity.setStreet(newEntity.street());
        entity.setHousenumber(newEntity.housenumber());
        entity.setPostalcode(newEntity.postalcode());
        entity.setCity(newEntity.city());
        entity.setGender(newEntity.gender());
        entity.setSupervising1(newEntity.supervising1());
        entity.setSupervising2(newEntity.supervising2());

        entity = this.patientService.save(entity);
        return this.assembler.toModel(this.patientService.toDto(entity, userDetails));
    }

    @GetMapping("{id}")
    EntityModel<PatientDto> one(@PathVariable UUID id, @AuthenticationPrincipal User userDetails) {
        Patient entity = this.patientService.findById(id).orElseThrow(() -> new PatientNotFoundException(id));
        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.VIEW, entity)) {
            throw new ForbiddenException("User does not have permission to view patients.");
        }
        return this.assembler.toModel(this.patientService.toDto(entity, userDetails));
    }

    @PutMapping("{id}")
    EntityModel<PatientDto> replaceEntity(
        @RequestBody PatientCreatePayload newEntity,
        @PathVariable UUID id,
        @AuthenticationPrincipal User userDetails
    ) {
        Patient entity = this.patientService.findById(id).orElseThrow(() -> new PatientNotFoundException(id));
        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.EDIT, entity)) {
            throw new ForbiddenException("User does not have permission to edit patients.");
        }

        entity.setFirstName(newEntity.firstName());
        entity.setLastName(newEntity.lastName());
        entity.setBirthdate(newEntity.birthdate());
        entity.setStreet(newEntity.street());
        entity.setHousenumber(newEntity.housenumber());
        entity.setPostalcode(newEntity.postalcode());
        entity.setCity(newEntity.city());
        entity.setGender(newEntity.gender());
        entity.setSupervising1(newEntity.supervising1());
        entity.setSupervising2(newEntity.supervising2());

        entity = this.patientService.save(entity);
        return this.assembler.toModel(this.patientService.toDto(entity, userDetails));
    }

    @DeleteMapping("{id}")
    void deleteEntity(@PathVariable UUID id, @AuthenticationPrincipal User userDetails) {
        Patient entity = this.patientService.findById(id).orElseThrow(() -> new PatientNotFoundException(id));
        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.DELETE, entity)) {
            throw new ForbiddenException("User does not have permission to delete patients.");
        }
        this.patientService.deleteById(id);
    }

    public record PatientCreatePayload(
        String firstName,
        String lastName,
        LocalDate birthdate,
        String street,
        String housenumber,
        String postalcode,
        String city,
        String gender,
        String supervising1,
        String supervising2
    ) {}
}
