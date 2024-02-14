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

import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.ldaptive.reactive.ReactiveLdaptiveTemplate;
import org.bremersee.ldaptive.security.authentication.ReactiveLdaptiveAuthenticationManager;
import org.bremersee.ldaptive.spring.boot.autoconfigure.app.PersonMapper;
import org.bremersee.ldaptive.spring.boot.autoconfigure.app.TestConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.TestSocketUtils;
import reactor.test.StepVerifier;

/**
 * The ldaptive integration test.
 *
 * @author Christian Bremer
 */
@SpringBootTest(
    classes = {TestConfiguration.class},
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.main.web-application-type=reactive",
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
        "bremersee.ldaptive.autentication-template=open_ldap",
        "bremersee.ldaptive.authentication.user-base-dn=ou=people,dc=bremersee,dc=org",
        "bremersee.ldaptive.authentication.password-attribute=",
        "bremersee.ldaptive.authentication.group-fetch-strategy=group_contains_users",
        "bremersee.ldaptive.authentication.group-base-dn=ou=groups,dc=bremersee,dc=org",
        "bremersee.ldaptive.authentication.group-object-class=groupOfUniqueNames",
    })
@ExtendWith({SoftAssertionsExtension.class})
@Slf4j
class ReactiveLdaptiveIntegrationTest {

  @Value("${spring.ldap.embedded.base-dn}")
  private String baseDn;

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private ReactiveLdaptiveTemplate ldaptiveTemplate;

  @Autowired
  private ReactiveLdaptiveAuthenticationManager authenticationManager;

  @Autowired
  private PersonMapper personMapper;

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

    Set<String> names = Set.of("anna", "gustav", "hans");
    // without mapper
    StepVerifier.create(ldaptiveTemplate.findAll(searchRequest))
        .assertNext(
            ldapEntry -> assertTrue(names.contains(ldapEntry.getAttribute("uid").getStringValue())))
        .assertNext(
            ldapEntry -> assertTrue(names.contains(ldapEntry.getAttribute("uid").getStringValue())))
        .assertNext(
            ldapEntry -> assertTrue(names.contains(ldapEntry.getAttribute("uid").getStringValue())))
        .verifyComplete();

    // with mapper
    StepVerifier.create(ldaptiveTemplate.findAll(searchRequest, personMapper))
        .assertNext(person -> assertTrue(names.contains(person.getUid())))
        .assertNext(person -> assertTrue(names.contains(person.getUid())))
        .assertNext(person -> assertTrue(names.contains(person.getUid())))
        .verifyComplete();
  }

  @Test
  void authenticate(SoftAssertions softly) {

    // set password
    String password = ldaptiveTemplate.generateUserPassword("uid=anna,ou=people," + baseDn)
        .block();

    List<? extends GrantedAuthority> expectedRoles = List.of(
        new SimpleGrantedAuthority("developers"),
        new SimpleGrantedAuthority("managers"));
    // authenticate successfully
    StepVerifier
        .create(authenticationManager
            .authenticate(new UsernamePasswordAuthenticationToken("anna", password)))
        .assertNext(authentication -> {
          softly
              .assertThat(authentication.isAuthenticated())
              .isTrue();
          softly
              .assertThat(authentication.getAuthorities().stream()
                  .map(grantedAuthority -> (GrantedAuthority) grantedAuthority)
                  .toList())
              .containsExactlyInAnyOrderElementsOf(expectedRoles);
        })
        .verifyComplete();
  }

}
