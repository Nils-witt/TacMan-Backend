package dev.nilswitt.tacman.api.rest.v1.map;

import dev.nilswitt.tacman.api.dtos.MapOverlayDto;
import dev.nilswitt.tacman.entities.MapOverlay;
import dev.nilswitt.tacman.entities.SecurityGroup;
import dev.nilswitt.tacman.entities.User;
import dev.nilswitt.tacman.entities.repositories.MapOverlayRepository;
import dev.nilswitt.tacman.exceptions.ForbiddenException;
import dev.nilswitt.tacman.exceptions.MapOverlayNotFoundException;
import dev.nilswitt.tacman.records.OverlayConfig;
import dev.nilswitt.tacman.security.PermissionVerifier;
import org.apache.commons.io.FileUtils;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("api/map/overlays")
public class MapOverlayController {

    private final MapOverlayRepository repository;
    private final MapOverlayModelAssembler assembler;
    private final PermissionVerifier permissionVerifier;
    private final OverlayConfig overlayConfig;

    public MapOverlayController(MapOverlayRepository repository, MapOverlayModelAssembler assembler, PermissionVerifier permissionVerifier, OverlayConfig overlayConfig) {
        this.repository = repository;
        this.assembler = assembler;
        this.permissionVerifier = permissionVerifier;
        this.overlayConfig = overlayConfig;
    }

    @GetMapping("")
    CollectionModel<EntityModel<MapOverlayDto>> all(@AuthenticationPrincipal User userDetails) {
        if (this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.VIEW, SecurityGroup.UserRoleTypeEnum.MAPOVERLAY)) {
            List<EntityModel<MapOverlayDto>> entities = this.repository.findAll().stream().map(mapOverlay -> {
                MapOverlayDto dto = mapOverlay.toDto();
                dto.setPermissions(this.permissionVerifier.getScopes(mapOverlay, userDetails));
                return dto;
            }).map(this.assembler::toModel).collect(Collectors.toList());
            return CollectionModel.of(entities, linkTo(methodOn(MapOverlayController.class).all(null)).withSelfRel());
        }

        return CollectionModel.of(this.permissionVerifier.getMapOverlaysForUser(userDetails).stream().map(mapOverlay -> {
            MapOverlayDto dto = mapOverlay.toDto();
            dto.setPermissions(this.permissionVerifier.getScopes(mapOverlay, userDetails));
            return dto;
        }).map(this.assembler::toModel).collect(Collectors.toList()), linkTo(methodOn(MapOverlayController.class).all(null)).withSelfRel());
    }

    @PostMapping("")
    EntityModel<MapOverlayDto> newEntity(@RequestBody MapOverlayDto newEntity, @AuthenticationPrincipal User userDetails) {
        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.CREATE, SecurityGroup.UserRoleTypeEnum.MAPOVERLAY)) {
            throw new ForbiddenException("User does not have permission to create overlays.");
        }
        MapOverlay entity = this.repository.save(MapOverlay.of(newEntity));
        MapOverlayDto dto = entity.toDto();
        dto.setPermissions(this.permissionVerifier.getScopes(entity, userDetails));
        return this.assembler.toModel(dto);
    }

    @GetMapping("{id}")
    EntityModel<MapOverlayDto> one(@PathVariable UUID id, @AuthenticationPrincipal User userDetails) {
        MapOverlay entity = this.repository.findById(id).orElseThrow(() -> new MapOverlayNotFoundException(id));
        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.VIEW, entity)) {
            throw new ForbiddenException("User does not have permission to view overlays.");
        }
        MapOverlayDto dto = entity.toDto();
        dto.setPermissions(this.permissionVerifier.getScopes(entity, userDetails));
        return this.assembler.toModel(dto);
    }

    @PutMapping("{id}")
    EntityModel<MapOverlayDto> replaceEntity(@RequestBody MapOverlayDto newEntity, @PathVariable UUID id, @AuthenticationPrincipal User userDetails) {
        MapOverlay entity = this.repository.findById(id).orElseThrow(() -> new MapOverlayNotFoundException(id));

        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.EDIT, entity)) {
            throw new ForbiddenException("User does not have permission to edit overlays.");
        }

        entity.setName(newEntity.getName());

        entity.setBaseUrl(newEntity.getBaseUrl());
        entity.setBasePath(newEntity.getBasePath());
        entity.setTilePathPattern(newEntity.getTilePathPattern());

        MapOverlay saved = this.repository.save(entity);
        MapOverlayDto dto = saved.toDto();
        dto.setPermissions(this.permissionVerifier.getScopes(saved, userDetails));
        return this.assembler.toModel(dto);
    }

    @DeleteMapping("{id}")
    void deleteEntity(@PathVariable UUID id, @AuthenticationPrincipal User userDetails) {
        MapOverlay entity = this.repository.findById(id).orElseThrow(() -> new MapOverlayNotFoundException(id));

        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.DELETE, entity)) {
            throw new ForbiddenException("User does not have permission to delete overlays.");
        }
        this.repository.deleteById(id);
    }

    @PostMapping("{id}/upload/{version}")
    EntityModel<MapOverlayDto> upload(@PathVariable UUID id, @PathVariable String version, @RequestParam("file") MultipartFile file, @AuthenticationPrincipal User userDetails) {
        MapOverlay entity = this.repository.findById(id).orElseThrow(() -> new MapOverlayNotFoundException(id));

        if (!this.permissionVerifier.hasAccess(userDetails, SecurityGroup.UserRoleScopeEnum.EDIT, entity)) {
            throw new ForbiddenException("User does not have permission to edit overlays.");
        }
        File destDir = Path.of(overlayConfig.basePath(), entity.getId().toString(), String.valueOf(version)).toFile();
        try {
            if (!destDir.exists()) {
                destDir.mkdirs();
            } else {
                FileUtils.deleteDirectory(destDir);
                destDir.mkdirs();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create directory for overlay upload.", e);
        }

        try {
            InputStream inputStream = file.getInputStream();

            byte[] buffer = new byte[1024];
            ZipInputStream zis = new ZipInputStream(inputStream);
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(destDir, zipEntry);
                if (zipEntry.getName().startsWith(".") || zipEntry.getName().startsWith("_")) {
                    // IGnore hidden files
                } else if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    // fix for Windows-created archives
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }
                    // write file content
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zipEntry = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException(e);
        }
        entity.setLayerVersion(Integer.parseInt(version));
        MapOverlay saved = this.repository.save(entity);
        MapOverlayDto dto = saved.toDto();
        dto.setPermissions(this.permissionVerifier.getScopes(saved, userDetails));
        return this.assembler.toModel(dto);
    }

    /**
     * Helper for unzipping
     *
     * @param destinationDir
     * @param zipEntry
     * @return
     * @throws IOException
     */
    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }
}
