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

import static java.util.Objects.isNull;

import java.io.Serial;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.bremersee.ldaptive.LdaptiveEntryMapper;
import org.ldaptive.LdapEntry;
import org.ldaptive.transcode.StringValueTranscoder;
import org.ldaptive.transcode.ValueTranscoder;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * The type Ldaptive authentication token.
 *
 * @author Christian Bremer
 */
public class LdaptiveAuthenticationToken extends AbstractAuthenticationToken {

  @Serial
  private static final long serialVersionUID = 2L;

  private static final StringValueTranscoder STRING_TRANSCODER = new StringValueTranscoder();

  private final transient LdapEntry user;

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
    this.user = user;
    this.username = username;
    this.realNameAttribute = realNameAttribute;
    this.emailAttribute = emailAttribute;
  }

  @Override
  public LdapEntry getPrincipal() {
    return user;
  }

  @Override
  public String getName() {
    return username;
  }

  /**
   * Gets attribute names.
   *
   * @return the attribute names
   */
  public List<String> getAttributeNames() {
    if (isNull(user)) {
      return List.of();
    }
    return Arrays.stream(user.getAttributeNames()).toList();
  }

  /**
   * Gets real name.
   *
   * @return the real name
   */
  public String getRealName() {
    return getAttributeValue(realNameAttribute, STRING_TRANSCODER);
  }

  /**
   * Gets email.
   *
   * @return the email
   */
  public String getEmail() {
    return getAttributeValues(emailAttribute, STRING_TRANSCODER)
        .stream()
        .findFirst()
        .orElse(null);
  }

  /**
   * Gets attribute value.
   *
   * @param <T> the type parameter
   * @param name the name; required
   * @param valueTranscoder the value transcoder; required
   * @return the attribute value
   */
  public <T> T getAttributeValue(
      String name,
      ValueTranscoder<T> valueTranscoder) {
    if (isNull(user)) {
      return null;
    }
    return LdaptiveEntryMapper.getAttributeValue(user, name, valueTranscoder, null);
  }

  /**
   * Gets attribute values.
   *
   * @param <T> the type parameter
   * @param name the name; required
   * @param valueTranscoder the value transcoder; required
   * @return the attribute values
   */
  public <T> Collection<T> getAttributeValues(
      String name,
      ValueTranscoder<T> valueTranscoder) {
    if (isNull(user)) {
      return List.of();
    }
    return LdaptiveEntryMapper.getAttributeValues(user, name, valueTranscoder);
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
