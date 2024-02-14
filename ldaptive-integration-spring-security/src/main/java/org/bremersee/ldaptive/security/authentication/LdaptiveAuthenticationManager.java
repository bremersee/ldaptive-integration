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
import static java.util.Objects.nonNull;
import static org.springframework.util.ObjectUtils.isEmpty;

import java.util.Collection;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import lombok.Setter;
import org.bremersee.ldaptive.LdaptiveConnectionConfigProvider;
import org.bremersee.ldaptive.LdaptiveConnectionFactoryProvider;
import org.bremersee.ldaptive.LdaptiveEntryMapper;
import org.bremersee.ldaptive.LdaptiveException;
import org.bremersee.ldaptive.LdaptiveTemplate;
import org.bremersee.ldaptive.security.authentication.templates.NoAccountControlEvaluator;
import org.ldaptive.CompareRequest;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.FilterTemplate;
import org.ldaptive.LdapEntry;
import org.ldaptive.ResultCode;
import org.ldaptive.SearchRequest;
import org.ldaptive.transcode.StringValueTranscoder;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

/**
 * The type Ldaptive authentication manager.
 *
 * @author Christian Bremer
 */
public class LdaptiveAuthenticationManager
    implements AuthenticationManager, AuthenticationProvider { // message source aware

  /**
   * The constant USERNAME_PLACEHOLDER.
   */
  protected static final String USERNAME_PLACEHOLDER = "${username}";

  /**
   * The constant STRING_TRANSCODER.
   */
  protected static final StringValueTranscoder STRING_TRANSCODER = new StringValueTranscoder();

  private final LdaptiveConnectionConfigProvider connectionConfigProvider;

  private final LdaptiveConnectionFactoryProvider connectionFactoryProvider;

  private final LdaptiveAuthenticationProperties authenticationProperties;

  @Setter
  private PasswordEncoder passwordEncoder;

  private AccountControlEvaluator accountControlEvaluator = new NoAccountControlEvaluator();

  /**
   * Instantiates a new Ldaptive authentication manager.
   *
   * @param connectionConfigProvider the connection config provider
   * @param connectionFactoryProvider the connection factory provider
   * @param authenticationProperties the authentication properties
   */
  public LdaptiveAuthenticationManager(LdaptiveConnectionConfigProvider connectionConfigProvider,
      LdaptiveConnectionFactoryProvider connectionFactoryProvider,
      LdaptiveAuthenticationProperties authenticationProperties) {
    this.connectionConfigProvider = connectionConfigProvider;
    this.connectionFactoryProvider = connectionFactoryProvider;
    this.authenticationProperties = authenticationProperties;
  }

  /**
   * Sets account control evaluator.
   *
   * @param accountControlEvaluator the account control evaluator
   */
  public void setAccountControlEvaluator(
      AccountControlEvaluator accountControlEvaluator) {
    if (nonNull(accountControlEvaluator)) {
      this.accountControlEvaluator = accountControlEvaluator;
    }
  }

  @Override
  public LdaptiveAuthenticationToken authenticate(Authentication authentication)
      throws AuthenticationException {
    String username = authentication.getName();
    String password = String.valueOf(authentication.getCredentials());
    ConnectionFactory connectionFactory = getConnectionFactory(username, password);
    try {
      LdaptiveTemplate ldaptiveTemplate = new LdaptiveTemplate(connectionFactory);
      LdapEntry user = getUser(ldaptiveTemplate, username);
      checkPassword(ldaptiveTemplate, user, password);
      checkAccountControl(user);
      Collection<? extends GrantedAuthority> roles = getAllRoles(ldaptiveTemplate, user);
      return new LdaptiveAuthenticationToken(
          user,
          LdaptiveEntryMapper.getAttributeValue(
              user, authenticationProperties.getUserUidAttribute(), STRING_TRANSCODER, username),
          authenticationProperties.getRealNameAttribute(),
          authenticationProperties.getEmailAttribute(),
          roles);

    } finally {
      connectionFactory.close();
    }
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class
        .isAssignableFrom(authentication);
  }

  private boolean bindWithAuthentication() {
    return isNull(authenticationProperties.getPasswordAttribute())
        || authenticationProperties.getPasswordAttribute().isBlank();
  }

  private ConnectionFactory getConnectionFactory(String username, String password) {
    ConnectionConfig connectionConfig;
    if (bindWithAuthentication()) {
      String bindDn = LdaptiveEntryMapper.createDn(
          authenticationProperties.getUserUidAttribute(),
          username,
          authenticationProperties.getUserBaseDn());
      connectionConfig = connectionConfigProvider.getConnectionConfig(bindDn, password);
    } else {
      connectionConfig = connectionConfigProvider.getConnectionConfig();
    }
    return connectionFactoryProvider.getDefaultConnectionFactory(connectionConfig);
  }

  /**
   * Gets user.
   *
   * @param ldaptiveTemplate the ldaptive template
   * @param username the username
   * @return the user
   * @throws UsernameNotFoundException the username not found exception
   */
  protected LdapEntry getUser(LdaptiveTemplate ldaptiveTemplate, String username)
      throws UsernameNotFoundException {

    try {
      return ldaptiveTemplate
          .findOne(
              SearchRequest.builder()
                  .dn(authenticationProperties.getUserBaseDn())
                  .filter(FilterTemplate.builder()
                      .filter(authenticationProperties.getUserFindOneFilter())
                      .parameters(username)
                      .build())
                  .scope(authenticationProperties.getUserFindOneSearchScope())
                  .sizeLimit(1)
                  .build())
          .orElseThrow(() -> new UsernameNotFoundException(
              "User '" + username + "' was not found."));

    } catch (LdaptiveException le) {
      throw getBindException(le);
    }
  }

  private RuntimeException getBindException(LdaptiveException exception) {
    BadCredentialsException badCredentials = new BadCredentialsException("Password doesn't match.");
    if (ResultCode.INVALID_CREDENTIALS.equals(exception.getResultCode())) {
      return badCredentials;
    }
    String message = Optional.ofNullable(exception.getLdapException())
        .map(Throwable::getMessage)
        .map(String::toLowerCase)
        .orElse("");
    String text = ("resultCode=" + ResultCode.INVALID_CREDENTIALS).toLowerCase();
    if (ResultCode.CONNECT_ERROR.equals(exception.getResultCode()) && message.contains(text)) {
      return badCredentials;
    }
    return exception;
  }

  /**
   * Check password.
   *
   * @param ldaptiveTemplate the ldaptive template
   * @param user the user
   * @param password the password
   */
  protected void checkPassword(
      LdaptiveTemplate ldaptiveTemplate,
      LdapEntry user,
      String password) {

    if (bindWithAuthentication()) {
      return;
    }
    Assert.notNull(passwordEncoder, "No password encoder is present.");
    boolean matches = ldaptiveTemplate.compare(CompareRequest.builder()
        .dn(user.getDn())
        .name(authenticationProperties.getPasswordAttribute())
        .value(passwordEncoder.encode(password))
        .build());
    if (!matches) {
      throw new BadCredentialsException("Password doesn't match.");
    }
  }

  /**
   * Check account control.
   *
   * @param user the user
   */
  protected void checkAccountControl(LdapEntry user) {
    if (!accountControlEvaluator.isEnabled(user)) {
      throw new DisabledException("Account is disabled.");
    }
    if (!accountControlEvaluator.isAccountNonLocked(user)) {
      throw new LockedException("Account is locked.");
    }
    if (!accountControlEvaluator.isAccountNonExpired(user)) {
      throw new AccountExpiredException("Account is expired.");
    }
    if (!accountControlEvaluator.isCredentialsNonExpired(user)) {
      throw new CredentialsExpiredException("Credentials are expired.");
    }
  }

  /**
   * Gets all roles.
   *
   * @param ldaptiveTemplate the ldaptive template
   * @param user the user
   * @return the all roles
   */
  protected Collection<? extends GrantedAuthority> getAllRoles(
      LdaptiveTemplate ldaptiveTemplate, LdapEntry user) {
    Stream<? extends GrantedAuthority> defaultRoles = Stream
        .ofNullable(authenticationProperties.getDefaultRoles())
        .flatMap(Collection::stream)
        .map(SimpleGrantedAuthority::new);
    Stream<? extends GrantedAuthority> fromGroups = getRoles(ldaptiveTemplate, user);
    return Stream.concat(defaultRoles, fromGroups)
        .distinct()
        .toList();
  }

  /**
   * Gets roles.
   *
   * @param ldaptiveTemplate the ldaptive template
   * @param user the user
   * @return the roles
   */
  protected Stream<? extends GrantedAuthority> getRoles(
      LdaptiveTemplate ldaptiveTemplate, LdapEntry user) {

    if (!authenticationProperties.isLdapGroupsToRolesMappingEnabled()) {
      return Stream.empty();
    }
    return switch (authenticationProperties.getGroupFetchStrategy()) {
      case USER_CONTAINS_GROUPS -> getRolesByGroupsInUser(user);
      case GROUP_CONTAINS_USERS -> getRolesByGroupsWithUser(ldaptiveTemplate, user);
    };
  }

  /**
   * Gets roles by groups in user.
   *
   * @param user the user
   * @return the roles by groups in user
   */
  protected Stream<? extends GrantedAuthority> getRolesByGroupsInUser(LdapEntry user) {
    return LdaptiveEntryMapper.getAttributeValues(
            user, authenticationProperties.getMemberAttribute(), STRING_TRANSCODER)
        .stream()
        .map(LdaptiveEntryMapper::getRdn)
        .map(this::toGrantedAuthority);
  }

  /**
   * Gets roles by groups with user.
   *
   * @param ldaptiveTemplate the ldaptive template
   * @param user the user
   * @return the roles by groups with user
   */
  protected Stream<? extends GrantedAuthority> getRolesByGroupsWithUser(
      LdaptiveTemplate ldaptiveTemplate, LdapEntry user) {
    return ldaptiveTemplate
        .findAll(
            SearchRequest.builder()
                .dn(authenticationProperties.getGroupBaseDn())
                .filter(FilterTemplate.builder()
                    .filter(getGroupFilter(user))
                    .build())
                .scope(authenticationProperties.getGroupSearchScope())
                .build())
        .stream()
        .map(this::getGroupName)
        .map(this::toGrantedAuthority);
  }

  /**
   * Gets group filter.
   *
   * @param user the user
   * @return the group filter
   */
  protected String getGroupFilter(LdapEntry user) {
    String groupObjectClass = authenticationProperties.getGroupObjectClass();
    String groupMemberAttribute = authenticationProperties.getGroupMemberAttribute();
    String groupMemberValue;
    String groupMemberFormat = authenticationProperties.getGroupMemberFormat();
    if (isEmpty(groupMemberFormat)) {
      groupMemberValue = user.getDn();
    } else {
      String username = getUsername(user);
      groupMemberValue = groupMemberFormat
          .replaceFirst(Pattern.quote(USERNAME_PLACEHOLDER), username);
    }
    return String.format("(&(objectClass=%s)(%s=%s))",
        groupObjectClass, groupMemberAttribute, groupMemberValue);
  }

  /**
   * Gets group name.
   *
   * @param group the group
   * @return the group name
   */
  protected String getGroupName(LdapEntry group) {
    String groupIdAttribute = authenticationProperties.getGroupIdAttribute();
    String fallback = LdaptiveEntryMapper.getRdn(group.getDn());
    if (isEmpty(groupIdAttribute)) {
      return fallback;
    }
    return LdaptiveEntryMapper
        .getAttributeValue(group, groupIdAttribute, STRING_TRANSCODER, fallback);
  }

  /**
   * To granted authority granted authority.
   *
   * @param role the role
   * @return the granted authority
   */
  protected GrantedAuthority toGrantedAuthority(String role) {
    return new SimpleGrantedAuthority(mapRole(role));
  }

  /**
   * Map role string.
   *
   * @param role the role
   * @return the string
   */
  protected String mapRole(String role) {
    return Optional.ofNullable(authenticationProperties.getGroupToRoleMapping())
        .flatMap(mapping -> Optional.ofNullable(mapping.get(role)))
        .orElse(normalizeRole(role));
  }

  /**
   * Normalize role string.
   *
   * @param roleName the role name
   * @return the string
   */
  protected String normalizeRole(String roleName) {
    String normalizedRoleName = roleName;
    if (!isEmpty(authenticationProperties.getRoleSpaceReplacement())) {
      normalizedRoleName = normalizedRoleName
          .replaceAll(Pattern.quote(" "), authenticationProperties.getRoleSpaceReplacement());
    }
    normalizedRoleName = switch (authenticationProperties.getRoleCaseTransformation()) {
      case NONE -> normalizedRoleName;
      case TO_LOWER_CASE -> normalizedRoleName.toLowerCase();
      case TO_UPPER_CASE -> normalizedRoleName.toUpperCase();
    };
    String prefix = authenticationProperties.getRolePrefix();
    if (isEmpty(prefix) || normalizedRoleName.startsWith(prefix)) {
      return normalizedRoleName;
    }
    return prefix + normalizedRoleName;
  }

  /**
   * Gets username.
   *
   * @param user the user
   * @return the username
   */
  protected String getUsername(LdapEntry user) {
    return LdaptiveEntryMapper.getAttributeValue(
        user, authenticationProperties.getUserUidAttribute(), STRING_TRANSCODER, null);
  }

}
