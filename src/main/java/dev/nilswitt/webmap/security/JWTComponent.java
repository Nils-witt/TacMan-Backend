package dev.nilswitt.webmap.security;


import dev.nilswitt.webmap.entities.User;
import dev.nilswitt.webmap.entities.repositories.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

@Component
@Log4j2
public class JWTComponent {

    private final long EXPIRATION_MS;

    private final UserRepository userRepository;
    private final SecretKey secretKey ;

    public JWTComponent(UserRepository userRepository, @Value("${application.security.jwt_secret}") String secret, @Value("${application.security.jwt_expiration_ms:10}") long expirationMs) {
        this.userRepository = userRepository;
        this.EXPIRATION_MS = expirationMs;
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }


    public String generateToken(User user) {
        HashMap<String, Object> claims = new HashMap<>();
        log.debug("Generating token for user {}: claims={}", user.getUsername(), claims);
        return Jwts.builder()
                .subject(user.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .claims(claims)
                .signWith(this.secretKey, Jwts.SIG.HS512)
                .compact();
    }

    public UUID extractUserId(String token) {


        return UUID.fromString(Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject());
    }


    public User getUserFromToken(String token) {
        UUID uuid = extractUserId(token);
        return userRepository.findById(uuid).orElse(null);
    }


}
