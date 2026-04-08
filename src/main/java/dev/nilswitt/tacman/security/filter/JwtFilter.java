package dev.nilswitt.tacman.security.filter;

import dev.nilswitt.tacman.entities.SecurityGroup;
import dev.nilswitt.tacman.entities.User;
import dev.nilswitt.tacman.security.JWTTokenComponent;
import dev.nilswitt.tacman.services.SecurityGroupService;
import dev.nilswitt.tacman.services.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Log4j2
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JWTTokenComponent jwtUtil;
    private final UserService userService;
    private final SecurityGroupService securityGroupService;

    public JwtFilter(JWTTokenComponent jwtUtil, UserService userService, SecurityGroupService securityGroupService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.securityGroupService = securityGroupService;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else {
            if (request.getParameter("token") != null) {
                token = request.getParameter("token");
            }
        }

        if (token != null && !token.isBlank()) {
            boolean isAuthenticated = false;
            try {
                User user = jwtUtil.getUserFromToken(token);
                AbstractAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    user,
                    token,
                    user.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
                isAuthenticated = true;
                log.info("Authenticated user: " + user.getUsername());
            } catch (Exception e) {
                log.warn("Local JWT validation failed: {}", e.getMessage());
            }
            if (isAuthenticated) {
                filterChain.doFilter(request, response);
                return;
            }
            try {
                User user = null;

                String username = jwtUtil.getUsernameFromSSOToken(token);
                if (username == null || username.isBlank()) {
                    log.warn("SSO JWT does not contain a valid username");
                    filterChain.doFilter(request, response);
                    return;
                }
                Optional<User> userOpt = userService.findByUsername(username);
                Claims claims = jwtUtil.getClaimsFromSSOToken(token);

                if (userOpt.isPresent()) {
                    User newUser = userOpt.get();
                    ArrayList<String> cGroups = (ArrayList<String>) claims.get("groups", ArrayList.class);
                    Set<SecurityGroup> securityGroups = newUser.getSecurityGroups();
                    cGroups.forEach(cGroup -> {
                        List<SecurityGroup> sg = securityGroupService.findBySsoGroupName(cGroup);
                        securityGroups.addAll(sg);
                    });

                    newUser.setSecurityGroups(securityGroups);
                    user = userService.save(newUser);
                } else {
                    log.info("Create new User: " + username);
                    try {
                        User newUser = new User();
                        newUser.setUsername(username);
                        newUser.setEmail(claims.get("email", String.class));
                        newUser.setFirstName(claims.get("given_name", String.class));
                        newUser.setLastName(claims.get("name", String.class));
                        ArrayList<String> cGroups = (ArrayList<String>) claims.get("groups", ArrayList.class);
                        Set<SecurityGroup> securityGroups = newUser.getSecurityGroups();
                        cGroups.forEach(cGroup -> {
                            List<SecurityGroup> sg = securityGroupService.findBySsoGroupName(cGroup);
                            securityGroups.addAll(sg);
                        });
                        securityGroupService.findByName("Everyone").ifPresent(securityGroups::add);

                        newUser.setSecurityGroups(securityGroups);
                        user = userService.save(newUser);
                    } catch (Exception e) {
                        log.warn("User creation failed: {}", e.getMessage());
                    }
                }

                AbstractAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    user,
                    token,
                    user.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                log.debug("SSO JWT validation failed: {}", e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }
}
