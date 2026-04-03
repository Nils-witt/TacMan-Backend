package dev.nilswitt.tacman.services;

import dev.nilswitt.tacman.api.dtos.SecurityGroupDto;
import dev.nilswitt.tacman.entities.SecurityGroup;
import dev.nilswitt.tacman.entities.User;
import dev.nilswitt.tacman.entities.repositories.SecurityGroupPermissionsRepository;
import dev.nilswitt.tacman.entities.repositories.SecurityGroupRepository;
import dev.nilswitt.tacman.security.PermissionVerifier;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class SecurityGroupService {

    private final SecurityGroupRepository securityGroupRepository;
    private final SecurityGroupPermissionsRepository securityGroupPermissionsRepository;
    private final PermissionVerifier permissionVerifier;

    public SecurityGroupService(
        SecurityGroupRepository securityGroupRepository,
        SecurityGroupPermissionsRepository securityGroupPermissionsRepository,
        PermissionVerifier permissionVerifier
    ) {
        this.securityGroupRepository = securityGroupRepository;
        this.securityGroupPermissionsRepository = securityGroupPermissionsRepository;
        this.permissionVerifier = permissionVerifier;
    }

    public List<SecurityGroup> findAll() {
        return securityGroupRepository.findAll();
    }

    public Optional<SecurityGroup> findById(UUID id) {
        return securityGroupRepository.findById(id);
    }

    public Optional<SecurityGroup> findByName(String name) {
        return securityGroupRepository.findByName(name);
    }

    public List<SecurityGroup> findBySsoGroupName(String ssoGroupName) {
        return securityGroupRepository.findBySsoGroupName(ssoGroupName);
    }

    public SecurityGroup save(SecurityGroup securityGroup) {
        return securityGroupRepository.save(securityGroup);
    }

    public void deleteById(UUID id) {
        securityGroupRepository.deleteById(id);
    }

    public void deletePermissionsBySecurityGroup(SecurityGroup securityGroup) {
        securityGroupPermissionsRepository.deleteBySecurityGroup(securityGroup);
    }

    public void removeFromAllUsers(UUID groupId) {
        securityGroupRepository.removeFromAllUsers(groupId);
    }

    public void removeFromAllOverlays(UUID groupId) {
        securityGroupRepository.removeFromAllOverlays(groupId);
    }

    public SecurityGroupDto toDto(SecurityGroup securityGroup, User actingUser) {
        SecurityGroupDto dto = new SecurityGroupDto(securityGroup);
        dto.setPermissions(this.permissionVerifier.getScopes(securityGroup, actingUser));
        return dto;
    }

    public SecurityGroup fromDto(SecurityGroupDto dto) {
        SecurityGroup securityGroup = new SecurityGroup();
        securityGroup.setName(dto.getName());
        securityGroup.setSsoGroupName(dto.getSsoGroupName());
        securityGroup.setRoles(new HashSet<>(dto.getRoles()));

        return securityGroup;
    }
}
