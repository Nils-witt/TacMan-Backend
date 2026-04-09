package dev.nilswitt.tacman.api.dtos;

import dev.nilswitt.tacman.entities.Patient;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PatientDto extends AbstractEntityDto {

    private String firstName;
    private String lastName;
    private LocalDate birthdate;
    private String street;
    private String housenumber;
    private String postalcode;
    private String city;
    private String gender;
    private String supervising1;
    private String supervising2;

    public PatientDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String createdBy,
        String modifiedBy,
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
    ) {
        super(id, createdAt, updatedAt, createdBy, modifiedBy);
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.street = street;
        this.housenumber = housenumber;
        this.postalcode = postalcode;
        this.city = city;
        this.gender = gender;
        this.supervising1 = supervising1;
        this.supervising2 = supervising2;
    }

    public PatientDto(Patient patient) {
        super(
            patient.getId(),
            patient.getCreatedAt(),
            patient.getUpdatedAt(),
            patient.getCreatedBy(),
            patient.getModifiedBy()
        );
        this.firstName = patient.getFirstName();
        this.lastName = patient.getLastName();
        this.birthdate = patient.getBirthdate();
        this.street = patient.getStreet();
        this.housenumber = patient.getHousenumber();
        this.postalcode = patient.getPostalcode();
        this.city = patient.getCity();
        this.gender = patient.getGender();
        this.supervising1 = patient.getSupervising1();
        this.supervising2 = patient.getSupervising2();
    }
}
