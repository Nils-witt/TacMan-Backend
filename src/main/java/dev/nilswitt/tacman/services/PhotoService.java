package dev.nilswitt.tacman.services;

import dev.nilswitt.tacman.api.dtos.PhotoDto;
import dev.nilswitt.tacman.entities.MissionGroup;
import dev.nilswitt.tacman.entities.Photo;
import dev.nilswitt.tacman.entities.User;
import dev.nilswitt.tacman.entities.repositories.PhotoRepository;
import dev.nilswitt.tacman.security.PermissionVerifier;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final PermissionVerifier permissionVerifier;
    private final MissionGroupService missionGroupService;
    private final UserService userService;

    public PhotoService(
        PhotoRepository photoRepository,
        PermissionVerifier permissionVerifier,
        MissionGroupService missionGroupService,
        UserService userService
    ) {
        this.photoRepository = photoRepository;
        this.permissionVerifier = permissionVerifier;
        this.missionGroupService = missionGroupService;
        this.userService = userService;
    }

    public List<Photo> findAll() {
        return photoRepository.findAll();
    }

    public Optional<Photo> findById(UUID id) {
        return photoRepository.findById(id);
    }

    public List<Photo> findByMissionGroup(MissionGroup missionGroup) {
        return photoRepository.findByMissionGroup(missionGroup);
    }

    public Photo save(Photo photo) {
        return photoRepository.save(photo);
    }

    public void deleteById(UUID id) {
        photoRepository.deleteById(id);
    }

    public PhotoDto toDto(Photo photo, User actingUser) {
        PhotoDto dto = new PhotoDto(photo);
        dto.setPermissions(this.permissionVerifier.getScopes(photo, actingUser));
        return dto;
    }

    public Photo fromDto(PhotoDto dto) {
        Photo photo = new Photo();
        photo.setName(dto.getName());
        photo.setAuthor(dto.getAuthorId() != null ? userService.findById(dto.getAuthorId()).orElse(null) : null);
        photo.setMissionGroup(
            dto.getMissionGroupId() != null ? missionGroupService.findById(dto.getMissionGroupId()).orElse(null) : null
        );

        return photo;
    }
}
