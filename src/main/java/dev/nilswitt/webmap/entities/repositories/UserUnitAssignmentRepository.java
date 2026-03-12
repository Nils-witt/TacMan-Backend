package dev.nilswitt.webmap.entities.repositories;

import dev.nilswitt.webmap.entities.Unit;
import dev.nilswitt.webmap.entities.User;
import dev.nilswitt.webmap.entities.UserUnitAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserUnitAssignmentRepository extends JpaRepository<UserUnitAssignment, UUID> {
    List<UserUnitAssignment> findByUser(User user);

    List<UserUnitAssignment> findByUserAndEndTimeNull(User user);

    List<UserUnitAssignment> findByUnit(Unit unit);

    List<UserUnitAssignment> findByUnitAndEndTimeNull(Unit unit);

}