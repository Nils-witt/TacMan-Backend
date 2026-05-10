package dev.nilswitt.tacman.entities.services;

import dev.nilswitt.tacman.entities.Unit;
import dev.nilswitt.tacman.entities.User;
import dev.nilswitt.tacman.entities.UserUnitAssignment;
import dev.nilswitt.tacman.entities.repositories.UserUnitAssignmentRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UserUnitAssignmentService {

    private final UserUnitAssignmentRepository userUnitAssignmentRepository;

    public UserUnitAssignmentService(UserUnitAssignmentRepository userUnitAssignmentRepository) {
        this.userUnitAssignmentRepository = userUnitAssignmentRepository;
    }

    public List<UserUnitAssignment> findAll() {
        return userUnitAssignmentRepository.findAll();
    }

    public Optional<UserUnitAssignment> findById(UUID id) {
        return userUnitAssignmentRepository.findById(id);
    }

    public UserUnitAssignment save(UserUnitAssignment assignment) {
        return userUnitAssignmentRepository.save(assignment);
    }

    public void deleteById(UUID id) {
        userUnitAssignmentRepository.deleteById(id);
    }

    public List<UserUnitAssignment> findByUser(User user) {
        return userUnitAssignmentRepository.findByUser(user);
    }

    public List<UserUnitAssignment> findByUserAndEndTimeNull(User user) {
        return userUnitAssignmentRepository.findByUserAndEndTimeNull(user);
    }

    public List<UserUnitAssignment> findByUnit(Unit unit) {
        return userUnitAssignmentRepository.findByUnit(unit);
    }

    public List<UserUnitAssignment> findByUnitAndEndTimeNull(Unit unit) {
        return userUnitAssignmentRepository.findByUnitAndEndTimeNull(unit);
    }
}
