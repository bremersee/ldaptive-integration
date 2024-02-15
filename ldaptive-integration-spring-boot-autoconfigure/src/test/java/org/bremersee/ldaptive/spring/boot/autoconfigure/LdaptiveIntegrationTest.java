/*
 * Copyright 2019-2020 the original author or authors.
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

package org.bremersee.ldaptive.spring.boot.autoconfigure;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.ldaptive.LdaptiveTemplate;
import org.bremersee.ldaptive.security.authentication.LdaptiveAuthenticationManager;
import org.bremersee.ldaptive.security.authentication.LdaptiveAuthenticationToken;
import org.bremersee.ldaptive.spring.boot.autoconfigure.app.GroupMapper;
import org.bremersee.ldaptive.spring.boot.autoconfigure.app.PersonMapper;
import org.bremersee.ldaptive.spring.boot.autoconfigure.app.TestConfiguration;
import org.bremersee.ldaptive.spring.boot.autoconfigure.security.WebSecurityConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldaptive.LdapEntry;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.TestSocketUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * The ldaptive integration test.
 *
 * @author Christian Bremer
 */
@SpringBootTest(
    classes = {TestConfiguration.class, WebSecurityConfiguration.class},
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.main.web-application-type=servlet",
        "spring.application.name=ldaptive-test",
        "spring.ldap.embedded.base-dn=dc=bremersee,dc=org",
        "spring.ldap.embedded.credential.username=uid=admin",
        "spring.ldap.embedded.credential.password=secret",
        "spring.ldap.embedded.ldif=classpath:schema.ldif",
        "spring.ldap.embedded.validation.enabled=false",
        "bremersee.ldaptive.enabled=true",
        "bremersee.ldaptive.config.ldap-url=ldap://localhost:${spring.ldap.embedded.port}",
        "bremersee.ldaptive.config.use-start-tls=false",
        "bremersee.ldaptive.config.bind-dn=uid=admin",
        "bremersee.ldaptive.config.bind-credentials=secret",
        "bremersee.ldaptive.config.pooled=false",
        "bremersee.ldaptive.authentication.template=open_ldap",
        "bremersee.ldaptive.authentication.config.user-base-dn=ou=people,dc=bremersee,dc=org",
        "bremersee.ldaptive.authentication.config.password-attribute=",
        "bremersee.ldaptive.authentication.config.role-case-transformation=to_upper_case",
        "bremersee.ldaptive.authentication.config.role-prefix=ROLE_",
    })
@ExtendWith({SoftAssertionsExtension.class})
@Slf4j
class LdaptiveIntegrationTest {

  @Value("${spring.ldap.embedded.base-dn}")
  private String baseDn;

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private LdaptiveTemplate ldaptiveTemplate;

  @Autowired
  private LdaptiveAuthenticationManager authenticationManager;

  @Autowired
  private GroupMapper groupMapper;

  @Autowired
  private PersonMapper personMapper;

  @LocalServerPort
  private int port;

  /**
   * Sets embedded ldap port.
   */
  @BeforeAll
  static void setEmbeddedLdapPort() {
    int embeddedLdapPort = TestSocketUtils.findAvailableTcpPort();
    System.setProperty("spring.ldap.embedded.port", String.valueOf(embeddedLdapPort));
  }

  /**
   * Find existing persons.
   */
  @Test
  void findExistingPersons() {
    SearchRequest searchRequest = SearchRequest.builder()
        .dn("ou=people," + baseDn)
        .filter("(objectclass=inetOrgPerson)")
        .scope(SearchScope.ONELEVEL)
        .build();

    // without mapper
    Collection<LdapEntry> entries = ldaptiveTemplate.findAll(searchRequest);
    entries.forEach(ldapEntry -> log.info("Ldap entry found with cn = {}",
        ldapEntry.getAttribute("cn").getStringValue()));
    assertTrue(entries.stream()
        .anyMatch(entry -> "Anna Livia Plurabelle"
            .equalsIgnoreCase(entry.getAttribute("cn").getStringValue())));
    assertTrue(entries.stream()
        .anyMatch(entry -> "Gustav Anias Horn"
            .equalsIgnoreCase(entry.getAttribute("cn").getStringValue())));
    assertTrue(entries.stream()
        .anyMatch(entry -> "Hans Castorp"
            .equalsIgnoreCase(entry.getAttribute("cn").getStringValue())));

    // with mapper
    assertTrue(ldaptiveTemplate.findAll(searchRequest, personMapper)
        .anyMatch(entry -> "Anna Livia Plurabelle"
            .equalsIgnoreCase(entry.getCn())));
    assertTrue(ldaptiveTemplate.findAll(searchRequest, personMapper)
        .anyMatch(entry -> "Gustav Anias Horn"
            .equalsIgnoreCase(entry.getCn())));
    assertTrue(ldaptiveTemplate.findAll(searchRequest, personMapper)
        .anyMatch(entry -> "Hans Castorp"
            .equalsIgnoreCase(entry.getCn())));
  }

  /**
   * Find existing groups.
   */
  @Test
  void findExistingGroups() {
    SearchRequest searchRequest = SearchRequest.builder()
        .dn("ou=groups," + baseDn)
        .filter("(objectclass=groupOfUniqueNames)")
        .scope(SearchScope.ONELEVEL)
        .build();

    // without mapper
    Collection<LdapEntry> entries = ldaptiveTemplate.findAll(searchRequest);
    entries.forEach(ldapEntry -> log.info("Ldap entry found with cn = {}",
        ldapEntry.getAttribute("cn").getStringValue()));
    assertTrue(entries.stream()
        .anyMatch(entry -> "developers"
            .equalsIgnoreCase(entry.getAttribute("cn").getStringValue())));
    assertTrue(entries.stream()
        .anyMatch(entry -> "managers"
            .equalsIgnoreCase(entry.getAttribute("cn").getStringValue())));

    // with mapper
    assertTrue(ldaptiveTemplate.findAll(searchRequest, groupMapper)
        .anyMatch(entry -> "developers"
            .equalsIgnoreCase(entry.getCn())));
    assertTrue(ldaptiveTemplate.findAll(searchRequest, groupMapper)
        .anyMatch(entry -> "managers"
            .equalsIgnoreCase(entry.getCn())));
  }

  @Test
  void authenticate(SoftAssertions softly) {

    // no password present: authentication fails
    softly
        .assertThatExceptionOfType(BadCredentialsException.class)
        .isThrownBy(() -> authenticationManager
            .authenticate(new UsernamePasswordAuthenticationToken("anna", "secret")));

    // set password
    String password = ldaptiveTemplate.generateUserPassword("uid=anna,ou=people," + baseDn);

    // authenticate successfully
    LdaptiveAuthenticationToken authenticationToken = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken("anna", password));

    softly
        .assertThat(authenticationToken.getName())
        .isEqualTo("anna");
    softly
        .assertThat(authenticationToken.isAuthenticated())
        .isTrue();
    List<? extends GrantedAuthority> expectedRoles = List.of(
        new SimpleGrantedAuthority("ROLE_DEVELOPERS"),
        new SimpleGrantedAuthority("ROLE_MANAGERS"));
    softly
        .assertThat(authenticationToken.getAuthorities())
        .containsExactlyInAnyOrderElementsOf(expectedRoles);
  }

  @Test
  void sayHelloEndpoint(SoftAssertions softly) {
    softly
        .assertThatExceptionOfType(HttpClientErrorException.class)
        .isThrownBy(() -> new RestTemplate()
            .getForObject("http://localhost:" + port + "/hello", String.class))
        .extracting(RestClientResponseException::getStatusCode)
        .isEqualTo(HttpStatus.UNAUTHORIZED);

    // set password
    String password = ldaptiveTemplate.generateUserPassword("uid=hans,ou=people," + baseDn);

    // get again
    String helloResponse = WebClient.builder()
        .baseUrl("http://localhost:" + port)
        .filter(ExchangeFilterFunctions.basicAuthentication("hans", password))
        .build()
        .get()
        .uri("/hello")
        .exchangeToMono(res -> res.bodyToMono(String.class))
        .block();
    softly
        .assertThat(helloResponse)
        .isEqualTo("Hello!");
  }

}
