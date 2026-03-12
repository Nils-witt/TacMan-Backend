package dev.nilswitt.webmap.api.controller;

import dev.nilswitt.webmap.api.exceptions.UnauthorizedException;
import dev.nilswitt.webmap.entities.User;
import dev.nilswitt.webmap.entities.repositories.UserRepository;
import dev.nilswitt.webmap.security.JWTComponent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Log4j2
@RestController
@RequestMapping("api/token")
public class AuthController {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JWTComponent jwtHandler;

    public AuthController(PasswordEncoder passwordEncoder, UserRepository userRepository, JWTComponent jwtHandler) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtHandler = jwtHandler;
    }


    @GetMapping
    Map<String, Object> validate(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        try {
            if (token == null) {
                throw new UnauthorizedException();
            }
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            User user = this.jwtHandler.getUserFromToken(token);
            return Map.of("valid", true, "user", user);
        } catch (Exception e) {
            log.info("Token validation failed: {}", e.getMessage());
            throw new UnauthorizedException();
        }
    }

    @PostMapping
    Map<String, Object> obtain(@RequestBody AuthRequest authRequest) {
        Optional<User> userOpt = userRepository.findByUsername(authRequest.username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(authRequest.password, user.getPassword())) {
                String token = this.jwtHandler.generateToken(user);
                return Map.of("token", token, "userId", user.getId());
            }
        }
        throw new UnauthorizedException();
    }


    static class AuthRequest {
        public String username;
        public String password;
    }
}
