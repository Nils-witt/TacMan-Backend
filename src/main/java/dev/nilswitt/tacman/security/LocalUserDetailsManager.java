package dev.nilswitt.tacman.security;

import dev.nilswitt.tacman.entities.User;
import dev.nilswitt.tacman.entities.repositories.UserRepository;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LocalUserDetailsManager implements UserDetailsService {

    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(LocalUserDetailsManager.class);

    public LocalUserDetailsManager(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        }

        return optionalUser.get();
    }
}
