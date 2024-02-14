/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
