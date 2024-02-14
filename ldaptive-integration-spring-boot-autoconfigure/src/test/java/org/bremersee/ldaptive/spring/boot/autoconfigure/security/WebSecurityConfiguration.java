package org.bremersee.ldaptive.spring.boot.autoconfigure.security;

import org.bremersee.ldaptive.security.authentication.LdaptiveAuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

  private final LdaptiveAuthenticationManager authenticationManager;

  public WebSecurityConfiguration(LdaptiveAuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authenticationManager(authenticationManager)
        .authorizeHttpRequests(authorize -> authorize
            .anyRequest().authenticated())
        .httpBasic(configurer -> configurer
            .realmName("junit"));
    return http.build();
  }

  @RestController
  public static class ExampleRestController {

    @GetMapping(path = "/hello")
    public ResponseEntity<String> sayHello() {
      return ResponseEntity.ok("Hello!");
    }
  }
}
