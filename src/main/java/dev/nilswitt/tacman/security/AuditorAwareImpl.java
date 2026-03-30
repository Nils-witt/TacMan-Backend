package dev.nilswitt.tacman.security;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            return Optional.of(SecurityContextHolder.getContext().getAuthentication().getName());
        } catch (NullPointerException e) {
            return Optional.of("system");
        }
    }
}
