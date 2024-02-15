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

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import org.bremersee.ldaptive.LdaptiveEntryMapper;

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
      return extractDomainName(properties.getUserBaseDn())
          .map(domain -> username + "@" + domain)
          .orElseThrow(() -> new IllegalStateException(String
              .format("Converting username %s to bind dn is not possible.", username)));
    }

    private static Optional<String> extractDomainName(String baseDn) {
      return Optional.ofNullable(baseDn)
          .map(dn -> {
            StringBuilder domainBuilder = new StringBuilder();
            String[] pairs = baseDn.split(Pattern.quote(","));
            for (String pair : pairs) {
              String[] parts = pair.split(Pattern.quote("="));
              if (parts.length != 2) {
                throw new IllegalArgumentException(String
                    .format("'%s' is not a parseable ldap base dn.", baseDn));
              }
              String name = parts[0].trim();
              String value = parts[1].trim();
              if ("dc".equalsIgnoreCase(name) && !value.isEmpty()) {
                if (!domainBuilder.isEmpty()) {
                  domainBuilder.append('.');
                }
                domainBuilder.append(value);
              }
            }
            return domainBuilder.toString();
          })
          .filter(domain -> !domain.isEmpty());
    }

  }

}
