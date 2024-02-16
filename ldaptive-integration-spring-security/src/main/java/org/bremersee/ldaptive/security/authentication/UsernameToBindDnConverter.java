/*
 * Copyright 2024 the original author or authors.
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

package org.bremersee.ldaptive.security.authentication;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bremersee.ldaptive.LdaptiveEntryMapper;
import org.ldaptive.dn.Dn;
import org.ldaptive.dn.NameValue;
import org.ldaptive.dn.RDn;

/**
 * Converts a username (like 'foobar') into it's bind dn (like
 * 'uid=foobar,ou=people,dc=example,dc=org').
 *
 * @author Christian Bremer
 */
@FunctionalInterface
public interface UsernameToBindDnConverter {

  /**
   * Converts a username (like 'foobar') into it's bind dn (like
   * 'uid=foobar,ou=people,dc=example,dc=org').
   *
   * @param username the username
   * @return the bind dn
   */
  String convert(String username);

  /**
   * The type By user rdn attribute.
   */
  class ByUserRdnAttribute implements UsernameToBindDnConverter {

    private final LdaptiveAuthenticationProperties properties;

    /**
     * Instantiates a new converter.
     *
     * @param properties the properties
     */
    public ByUserRdnAttribute(LdaptiveAuthenticationProperties properties) {
      this.properties = Objects
          .requireNonNull(properties, "Ldaptive authentication properties are required.");
    }

    @Override
    public String convert(String username) {
      return LdaptiveEntryMapper
          .createDn(properties.getUserRdnAttribute(), username, properties.getUserBaseDn());
    }
  }

  /**
   * Converts a username (like 'foobar') into it's (active directory) bind dn (like
   * 'foobar@example.org').
   *
   * @author Christian Bremer
   */
  class ByDomainEmail implements UsernameToBindDnConverter {

    private final LdaptiveAuthenticationProperties properties;

    /**
     * Instantiates a new converter.
     *
     * @param properties the properties
     */
    public ByDomainEmail(LdaptiveAuthenticationProperties properties) {
      this.properties = Objects
          .requireNonNull(properties, "Ldaptive authentication properties are required.");
    }

    @Override
    public String convert(String username) {
      return Optional.of(extractDomainName(properties.getUserBaseDn()))
          .filter(domain -> !domain.isEmpty())
          .map(domain -> username + "@" + domain)
          .orElseThrow(() -> new IllegalStateException(String
              .format("Converting username %s to bind dn is not possible.", username)));
    }

    private static String extractDomainName(String baseDn) {
      return Stream.ofNullable(baseDn)
          .map(Dn::new)
          .map(Dn::getRDns)
          .flatMap(Collection::stream)
          .map(RDn::getNameValue)
          .filter(nameValue -> nameValue.hasName("dc"))
          .map(NameValue::getStringValue)
          .collect(Collectors.joining("."));
    }

  }

}
