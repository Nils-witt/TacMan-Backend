package dev.nilswitt.tacman.api.rest.v1;

import dev.nilswitt.tacman.api.dtos.UserDto;
import dev.nilswitt.tacman.entities.User;
import dev.nilswitt.tacman.services.UserService;
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
    private final UserService userService;

    public ProfileController(UserModelAssembler assembler, UserService userService) {
        this.assembler = assembler;
        this.userService = userService;
    }

    @GetMapping("")
    EntityModel<UserDto> newEntity(@AuthenticationPrincipal User userDetails) {
        return this.assembler.toModel(this.userService.toDto(userDetails, userDetails));
    }
}
