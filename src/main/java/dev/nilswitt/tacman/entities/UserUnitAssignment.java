package dev.nilswitt.tacman.entities;

import dev.nilswitt.tacman.api.dtos.AbstractEntityDto;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserUnitAssignment extends AbstractEntity {

  @ManyToOne(optional = false)
  private User user;

  @ManyToOne(optional = false)
  private Unit unit;

  @Column(nullable = false)
  private Instant startTime;

  @Column
  private Instant endTime;

  @Override
  public AbstractEntityDto toDto() {
    return null;
  }
}
