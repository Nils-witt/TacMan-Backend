package dev.nilswitt.tacman.services;

import dev.nilswitt.tacman.api.dtos.UserDto;
import dev.nilswitt.tacman.entities.User;
import dev.nilswitt.tacman.entities.repositories.UserRepository;
import dev.nilswitt.tacman.security.PermissionVerifier;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PermissionVerifier permissionVerifier;
    private final SecurityGroupService securityGroupService;
    private final UnitService unitService;

    public UserService(
        UserRepository userRepository,
        PermissionVerifier permissionVerifier,
        SecurityGroupService securityGroupService,
        UnitService unitService
    ) {
        this.userRepository = userRepository;
        this.permissionVerifier = permissionVerifier;
        this.securityGroupService = securityGroupService;
        this.unitService = unitService;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }

    public UserDto toDto(User user, User actingUser) {
        UserDto dto = new UserDto(user);
        dto.setPermissions(this.permissionVerifier.getScopes(user, actingUser));
        return dto;
    }

    public User fromDto(UserDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setLastName(dto.getLastName());
        user.setFirstName(dto.getFirstName());
        user.setEmail(dto.getEmail());

        if (dto.getSecurityGroups() != null) {
            dto
                .getSecurityGroups()
                .forEach(groupId -> securityGroupService.findById(groupId).ifPresent(user::addSecurityGroup));
        } else {
            user.getSecurityGroups().clear();
        }
        securityGroupService.findByName("Everyone").ifPresent(user::addSecurityGroup);

        if (dto.getUnitId() != null) {
            user.setUnit(unitService.findById(dto.getUnitId()).orElse(null));
        } else {
            user.setUnit(null);
        }
        user.setEnabled(dto.isEnabled());
        user.setLocked(dto.isLocked());
        return user;
    }
}
