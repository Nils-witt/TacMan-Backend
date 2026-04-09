package dev.nilswitt.tacman.services;

import dev.nilswitt.tacman.api.dtos.PatientDto;
import dev.nilswitt.tacman.entities.Patient;
import dev.nilswitt.tacman.entities.User;
import dev.nilswitt.tacman.entities.repositories.PatientRepository;
import dev.nilswitt.tacman.entities.repositories.UserRepository;
import dev.nilswitt.tacman.security.PermissionVerifier;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final PermissionVerifier permissionVerifier;
    private final UserRepository userRepository;

    public PatientService(
        PatientRepository patientRepository,
        PermissionVerifier permissionVerifier,
        UserRepository userRepository
    ) {
        this.patientRepository = patientRepository;
        this.permissionVerifier = permissionVerifier;
        this.userRepository = userRepository;
    }

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public Optional<Patient> findById(UUID id) {
        return patientRepository.findById(id);
    }

    public Patient save(Patient patient) {
        return patientRepository.save(patient);
    }

    public void deleteById(UUID id) {
        patientRepository.deleteById(id);
    }

    public PatientDto toDto(Patient patient, User actingUser) {
        PatientDto dto = new PatientDto(patient);
        dto.setPermissions(this.permissionVerifier.getScopes(patient, actingUser));
        return dto;
    }

    public Patient fromDto(PatientDto dto) {
        Patient patient = new Patient();
        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setBirthdate(dto.getBirthdate());
        patient.setStreet(dto.getStreet());
        patient.setHousenumber(dto.getHousenumber());
        patient.setPostalcode(dto.getPostalcode());
        patient.setCity(dto.getCity());
        patient.setGender(dto.getGender());
        patient.setSupervising1(dto.getSupervising1());
        patient.setSupervising2(dto.getSupervising2());
        return patient;
    }
}
