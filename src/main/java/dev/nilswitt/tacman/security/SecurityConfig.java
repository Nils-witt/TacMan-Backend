package dev.nilswitt.tacman.security;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import dev.nilswitt.tacman.security.filter.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
class SecurityConfig {

  private final JwtFilter jwtFilter;

  public SecurityConfig(JwtFilter jwtFilter) {
    this.jwtFilter = jwtFilter;
  }

  /**
   * Controls the access to the Websocket
   *
   * @param http
   * @return
   * @throws Exception
   */
  @Order(0)
  @Bean
  SecurityFilterChain wsFilterChain(HttpSecurity http) throws Exception {
    return http
      .securityMatcher("/api/ws/**")
      .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
      .build();
  }

  /**
   * Controls the access to the REST API
   *
   * @param http
   * @return
   * @throws Exception
   */
  @Order(1)
  @Bean
  SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
    return http
      .securityMatcher("/api/**")
      .csrf(AbstractHttpConfigurer::disable)
      .authorizeHttpRequests(auth -> {
        auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
        auth.requestMatchers("/api/token/**").permitAll();
        auth.requestMatchers("/api").permitAll();
        auth.anyRequest().authenticated();
      })
      .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
      .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
      .exceptionHandling(exception ->
        exception.authenticationEntryPoint(
          new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
        )
      )
      .build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
