package dev.nilswitt.webmap.security;

import dev.nilswitt.webmap.entities.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log4j2
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JWTComponent jwtUtil;

    public JwtFilter(JWTComponent jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

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
            try {
                User user = jwtUtil.getUserFromToken(token);

                AbstractAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                log.warn("JWT validation failed: {}", e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }
}
