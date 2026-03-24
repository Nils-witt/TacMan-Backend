package dev.nilswitt.tacman.entities.repositories;

import dev.nilswitt.tacman.entities.SecurityGroup;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityGroupRepository
  extends JpaRepository<SecurityGroup, UUID>
{
  Optional<SecurityGroup> findByName(String name);

  List<SecurityGroup> findBySsoGroupName(String ssoGroupName);
}
