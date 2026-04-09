package dev.nilswitt.tacman.entities;

import dev.nilswitt.tacman.entities.eventListeners.EntityEventListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@EntityListeners(EntityEventListener.class)
@Getter
@Setter
public class Patient extends AbstractEntity {

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String firstName;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String lastName;

    @Column
    private LocalDate birthdate;

    @Size(max = 255)
    @Column(length = 255)
    private String street;

    @Size(max = 20)
    @Column(length = 20)
    private String housenumber;

    @Size(max = 20)
    @Column(length = 20)
    private String postalcode;

    @Size(max = 100)
    @Column(length = 100)
    private String city;

    @Size(max = 50)
    @Column(length = 50)
    private String gender;

    @Column
    private String supervising1;

    @Column
    private String supervising2;
}
