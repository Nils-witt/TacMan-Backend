package dev.nilswitt.tacman.records;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public record PasswordResetConfig(
        @Value("${application.password-reset.base-url:http://localhost:8080}") String baseUrl,
        @Value("${application.password-reset.ttl-minutes:30}") long ttlMinutes,
        @Value("${application.password-reset.mail-from:no-reply@localhost}") String mailFrom
) {
}


