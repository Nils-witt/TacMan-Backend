package dev.nilswitt.tacman.security.jwt;

import dev.nilswitt.tacman.entities.JWTTokenRegistration;
import dev.nilswitt.tacman.entities.User;
import dev.nilswitt.tacman.services.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class JWTTokenComponent {

    private final long EXPIRATION_MS;

    private final UserService userService;
    private final SecretKey secretKey;
    private final JWTRegistry jwtRegistry;
    private PublicKey ssoJWKS = null;

    public JWTTokenComponent(
        UserService userService,
        JWTRegistry jwtRegistry,
        @Value("${application.security.jwt_secret}") String secret,
        @Value("${application.security.jwt_expiration_ms:10}") long expirationMs,
        @Value("${application.openid.jwks}") String jwks
    ) {
        this.jwtRegistry = jwtRegistry;
        this.userService = userService;
        this.EXPIRATION_MS = expirationMs;
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        try {
            byte[] data = Base64.getDecoder().decode((jwks.getBytes()));
            X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            this.ssoJWKS = fact.generatePublic(spec);
            log.info("SSO JWT loaded");
        } catch (Exception e) {
            /* */
        }
    }

    public String generateToken(User user, UUID tokenId) {
        return generateToken(user, tokenId, true);
    }

    public String generateToken(User user, UUID tokenId, boolean addToRegistry) {
        HashMap<String, Object> claims = new HashMap<>();
        log.debug("Generating token for user {}: claims={}", user.getUsername(), claims);
        claims.put("token_id", tokenId);
        Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION_MS);
        if (addToRegistry) {
            jwtRegistry.addToken(new JWTTokenRegistration(tokenId, user.getId(), expirationDate.toInstant()));
        }
        return Jwts.builder()
            .subject(user.getId().toString())
            .issuedAt(new Date())
            .expiration(expirationDate)
            .claims(claims)
            .signWith(this.secretKey, Jwts.SIG.HS512)
            .compact();
    }

    public UUID extractUserId(String token) throws ExpiredJwtException {
        try {
            Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
            UUID tokenId = UUID.fromString((String) claims.get("token_id"));
            if (!this.jwtRegistry.isValid(tokenId)) {
                throw new ExpiredJwtException(null, null, "Token has been revoked");
            }

            return UUID.fromString(claims.getSubject());
        } catch (IllegalArgumentException e) {
            throw new ExpiredJwtException(null, null, "Invalid JWT token: " + e.getMessage());
        }
    }

    public User getUserFromToken(String token) {
        UUID uuid = extractUserId(token);
        return userService.findById(uuid).orElse(null);
    }

    public String getUsernameFromSSOToken(String token) {
        try {
            return Jwts.parser()
                .verifyWith(this.ssoJWKS)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("preferred_username", String.class);
        } catch (Exception e) {
            // log.error(e.getMessage(), e);
        }
        return null;
    }

    public Claims getClaimsFromSSOToken(String token) {
        try {
            return Jwts.parser().verifyWith(this.ssoJWKS).build().parseSignedClaims(token).getPayload();
        } catch (Exception e) {
            // log.error(e.getMessage(), e);
        }
        return null;
    }
}
