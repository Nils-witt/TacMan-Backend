package dev.nilswitt.tacman.api.rest.v1;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import dev.nilswitt.tacman.api.dtos.PhotoDto;
import dev.nilswitt.tacman.entities.*;
import dev.nilswitt.tacman.exceptions.ForbiddenException;
import dev.nilswitt.tacman.exceptions.PhotoNotFoundException;
import dev.nilswitt.tacman.records.PictureConfig;
import dev.nilswitt.tacman.security.PermissionVerifier;
import dev.nilswitt.tacman.services.MissionGroupService;
import dev.nilswitt.tacman.services.PhotoService;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@RestController
@RequestMapping("api/photos")
public class PhotoController {

    private final PhotoModelAssembler assembler;
    private final PhotoService photoService;
    private final PictureConfig pictureConfig;
    private final PermissionVerifier permissionVerifier;
    private final MissionGroupService missionGroupService;

    public PhotoController(
        PhotoModelAssembler assembler,
        PhotoService photoService,
        PictureConfig pictureConfig,
        PermissionVerifier permissionVerifier,
        MissionGroupService missionGroupService
    ) {
        this.assembler = assembler;
        this.photoService = photoService;
        this.pictureConfig = pictureConfig;
        this.permissionVerifier = permissionVerifier;
        this.missionGroupService = missionGroupService;
    }

    @GetMapping("")
    CollectionModel<EntityModel<PhotoDto>> all(@AuthenticationPrincipal User userDetails) {
        if (
            this.permissionVerifier.hasAccess(
                userDetails,
                SecurityGroup.UserRoleScopeEnum.VIEW,
                SecurityGroup.UserRoleTypeEnum.PHOTO
            )
        ) {
            List<EntityModel<PhotoDto>> entities = this.photoService.findAll()
                .stream()
                .map(entity -> this.photoService.toDto(entity, userDetails))
                .map(this.assembler::toModel)
                .collect(Collectors.toList());
            return CollectionModel.of(entities, linkTo(methodOn(PhotoController.class).all(null)).withSelfRel());
        }

        return CollectionModel.of(
            this.permissionVerifier.getPhotosForUser(userDetails)
                .stream()
                .map(entity -> this.photoService.toDto(entity, userDetails))
                .map(this.assembler::toModel)
                .collect(Collectors.toList()),
            linkTo(methodOn(PhotoController.class).all(null)).withSelfRel()
        );
    }

    @PostMapping("")
    EntityModel<PhotoDto> newEntity(
        @RequestParam("file") MultipartFile file,
        @RequestParam("latitude") Double latitude,
        @RequestParam("longitude") Double longitude,
        @RequestParam("missionGroupId") UUID missionGroupId,
        @AuthenticationPrincipal User userDetails
    ) {
        if (missionGroupId == null) {
            throw new RuntimeException("Mission Group ID is required to upload a photo.");
        }
        MissionGroup missionGroup = this.missionGroupService.findById(missionGroupId).orElseThrow(() ->
            new PhotoNotFoundException(missionGroupId)
        );

        if (userDetails.getUnit() != null) {
            if (
                userDetails.getUnit().getMissionGroup() == null ||
                !userDetails.getUnit().getMissionGroup().getId().equals(missionGroup.getId())
            ) {
                if (
                    !this.permissionVerifier.hasAccess(
                        userDetails,
                        SecurityGroup.UserRoleScopeEnum.CREATE,
                        SecurityGroup.UserRoleTypeEnum.PHOTO
                    )
                ) {
                    throw new ForbiddenException("User does not have permission to create photos.");
                }
            }
        } else {
            if (
                !this.permissionVerifier.hasAccess(
                    userDetails,
                    SecurityGroup.UserRoleScopeEnum.CREATE,
                    SecurityGroup.UserRoleTypeEnum.PHOTO
                )
            ) {
                throw new ForbiddenException("User does not have permission to create photos.");
            }
        }

        EmbeddedPosition position = new EmbeddedPosition();
        position.setLatitude(latitude);
        position.setLongitude(longitude);
        position.setAccuracy(0.0);
        position.setTimestamp(Instant.now());

        Photo newPhoto = new Photo();
        newPhoto.setAuthor(userDetails);
        newPhoto.setMissionGroup(missionGroupService.findById(missionGroupId).orElse(null));
        newPhoto.setPosition(position);
        newPhoto = this.photoService.save(newPhoto);
        String fileExtension = Objects.requireNonNull(file.getOriginalFilename()).substring(
            file.getOriginalFilename().lastIndexOf(".")
        );
        try {
            String dailyPath =
                pictureConfig.localPath() +
                "/" +
                LocalDateTime.now().getYear() +
                "/" +
                LocalDateTime.now().getMonthValue() +
                "/" +
                LocalDateTime.now().getDayOfMonth();
            Files.createDirectories(Path.of(dailyPath));

            String filePath = dailyPath + "/" + newPhoto.getId() + fileExtension;
            FileOutputStream fos = new FileOutputStream(filePath);
            file.getInputStream().transferTo(fos);
            fos.close();
            newPhoto.setPath(
                LocalDateTime.now().getYear() +
                    "/" +
                    LocalDateTime.now().getMonthValue() +
                    "/" +
                    LocalDateTime.now().getDayOfMonth() +
                    "/" +
                    newPhoto.getId() +
                    fileExtension
            );
            newPhoto.setName("Photo " + newPhoto.getId());

            newPhoto = photoService.save(newPhoto);

            return this.assembler.toModel(this.photoService.toDto(newPhoto, userDetails));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @PatchMapping("{id}")
    EntityModel<PhotoDto> updateEntity(
        @PathVariable UUID id,
        @RequestBody String rawBody,
        @AuthenticationPrincipal User userDetails
    ) throws IOException {
        Photo entity = this.photoService.findById(id).orElseThrow(() -> new PhotoNotFoundException(id));
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode data = mapper.readTree(rawBody);
            if (data.has("name")) {
                entity.setName(data.get("name").asString());
            }
            if (data.has("position")) {
                JsonNode positionNode = data.get("position");
                if (positionNode.isObject()) {
                    if (entity.getPosition() == null) {
                        entity.setPosition(new EmbeddedPosition());
                    }
                    if (positionNode.has("latitude")) {
                        entity.getPosition().setLatitude(positionNode.get("latitude").asDouble());
                    }
                    if (positionNode.has("longitude")) {
                        entity.getPosition().setLongitude(positionNode.get("longitude").asDouble());
                    }
                    if (positionNode.has("altitude")) {
                        entity.getPosition().setAltitude(positionNode.get("altitude").asDouble());
                    }
                    if (positionNode.has("timestamp")) {
                        entity.getPosition().setTimestamp(Instant.parse(positionNode.get("timestamp").asString()));
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        Photo saved = photoService.save(entity);
        return this.assembler.toModel(this.photoService.toDto(saved, userDetails));
    }

    @GetMapping("{id}")
    EntityModel<PhotoDto> getEntity(@PathVariable UUID id, @AuthenticationPrincipal User userDetails) {
        Photo entity = this.photoService.findById(id).orElseThrow(() -> new PhotoNotFoundException(id));
        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.VIEW, entity)) {
            throw new ForbiddenException("User does not have permission to view photos.");
        }

        return this.assembler.toModel(this.photoService.toDto(entity, userDetails));
    }

    @GetMapping("{id}/image")
    ResponseEntity<Resource> getEntityPhoto(@PathVariable UUID id, @AuthenticationPrincipal User userDetails) {
        Photo entity = this.photoService.findById(id).orElseThrow(() -> new PhotoNotFoundException(id));
        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.VIEW, entity)) {
            throw new ForbiddenException("User does not have permission to view photos.");
        }

        try {
            Path path = Paths.get(pictureConfig.localPath() + "/" + entity.getPath());
            String mimeType = Files.probeContentType(path);
            Resource resource = new UrlResource(path.toUri());

            return ResponseEntity.ok().header("Content-Type", mimeType).body(resource);
        } catch (Exception e) {
            log.error("Error loading photo: {}", "Not Found in Filesystem");

            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("{id}")
    void deleteEntity(@PathVariable UUID id, @AuthenticationPrincipal User userDetails) {
        Photo entity = this.photoService.findById(id).orElseThrow(() -> new PhotoNotFoundException(id));

        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.DELETE, entity)) {
            throw new ForbiddenException("User does not have permission to delete photos.");
        }
        try {
            Files.deleteIfExists(Path.of(pictureConfig.localPath() + "/" + entity.getPath()));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        this.photoService.deleteById(id);
    }
}
