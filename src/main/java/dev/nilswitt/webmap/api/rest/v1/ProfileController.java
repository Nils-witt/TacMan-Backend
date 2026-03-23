package dev.nilswitt.webmap.api.rest.v1;

import dev.nilswitt.webmap.api.dtos.UserDto;
import dev.nilswitt.webmap.entities.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/profile")
@Log4j2
public class ProfileController {

  private final UserModelAssembler assembler;

  public ProfileController(UserModelAssembler assembler) {
    this.assembler = assembler;
  }

  @GetMapping("")
  EntityModel<UserDto> newEntity(@AuthenticationPrincipal User userDetails) {
    return this.assembler.toModel(userDetails.toDto());
  }
}
