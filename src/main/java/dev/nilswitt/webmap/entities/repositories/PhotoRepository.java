package dev.nilswitt.webmap.entities.repositories;

import dev.nilswitt.webmap.entities.MissionGroup;
import dev.nilswitt.webmap.entities.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PhotoRepository extends JpaRepository<Photo, UUID> {
    List<Photo> findByMissionGroup(MissionGroup missionGroup);


}