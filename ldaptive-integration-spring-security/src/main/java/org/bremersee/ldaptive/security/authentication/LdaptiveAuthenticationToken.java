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

package org.bremersee.ldaptive.security.authentication;

import java.io.Serial;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import org.bremersee.ldaptive.serializable.SerLdapAttr;
import org.ldaptive.LdapEntry;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * The type Ldaptive authentication token.
 *
 * @author Christian Bremer
 */
public class LdaptiveAuthenticationToken extends AbstractAuthenticationToken {

  @Serial
  private static final long serialVersionUID = 1L;

  private final Map<String, SerLdapAttr> ldapEntry;

  private final String username;

  private final String realNameAttribute;

  private final String emailAttribute;

  /**
   * Creates a token with the supplied array of authorities.
   *
   * @param user the user
   * @param username the username
   * @param realNameAttribute the real name attribute
   * @param emailAttribute the email attribute
   * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
   *     represented by this authentication object.
   */
  public LdaptiveAuthenticationToken(
      LdapEntry user,
      String username,
      String realNameAttribute,
      String emailAttribute,
      Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.ldapEntry = SerLdapAttr.toMap(user);
    this.username = username;
    this.realNameAttribute = realNameAttribute;
    this.emailAttribute = emailAttribute;
  }

  @Override
  public Map<String, SerLdapAttr> getPrincipal() {
    return ldapEntry;
  }

  @Override
  public String getName() {
    return username;
  }

  /**
   * Gets real name.
   *
   * @return the real name
   */
  public String getRealName() {
    return Optional.ofNullable(ldapEntry.get(realNameAttribute))
        .map(SerLdapAttr::getStringValue)
        .orElse(null);
  }

  /**
   * Gets email.
   *
   * @return the email
   */
  public String getEmail() {
    return Optional.ofNullable(ldapEntry.get(emailAttribute))
        .map(SerLdapAttr::getStringValue)
        .orElse(null);
  }

  @Override
  public boolean isAuthenticated() {
    return true;
  }

  @Override
  public Object getCredentials() {
    return null;
  }

}
