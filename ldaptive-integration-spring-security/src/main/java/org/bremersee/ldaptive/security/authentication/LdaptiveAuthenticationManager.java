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
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import lombok.Setter;
import org.bremersee.ldaptive.LdaptiveEntryMapper;
import org.bremersee.ldaptive.LdaptiveException;
import org.bremersee.ldaptive.LdaptiveTemplate;
import org.bremersee.ldaptive.security.authentication.LdaptiveAuthenticationProperties.GroupToRoleMapping;
import org.bremersee.ldaptive.security.authentication.LdaptiveAuthenticationProperties.StringReplacement;
import org.bremersee.ldaptive.security.authentication.templates.NoAccountControlEvaluator;
import org.ldaptive.BindConnectionInitializer;
import org.ldaptive.CompareRequest;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.DefaultConnectionFactory;
import org.ldaptive.FilterTemplate;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
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

  private final ConnectionConfig connectionConfig;

  private final LdaptiveAuthenticationProperties authenticationProperties;

  private UsernameToBindDnConverter usernameToBindDnConverter;

  @Setter
  private PasswordEncoder passwordEncoder;

  private AccountControlEvaluator accountControlEvaluator = new NoAccountControlEvaluator();

  /**
   * Instantiates a new Ldaptive authentication manager.
   *
   * @param connectionConfig the connection config
   * @param authenticationProperties the authentication properties
   */
  public LdaptiveAuthenticationManager(
      ConnectionConfig connectionConfig,
      LdaptiveAuthenticationProperties authenticationProperties) {
    this.connectionConfig = connectionConfig;
    this.authenticationProperties = authenticationProperties;
    this.usernameToBindDnConverter = authenticationProperties
        .getUsernameToBindDnConverter()
        .apply(authenticationProperties);
  }

  public void setUsernameToBindDnConverter(
      UsernameToBindDnConverter usernameToBindDnConverter) {
    if (nonNull(usernameToBindDnConverter)) {
      this.usernameToBindDnConverter = usernameToBindDnConverter;
    }
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

  void init() {
    if (!bindWithAuthentication() && isNull(passwordEncoder)) {
      throw new IllegalStateException(String.format("A password attribute is set (%s) but no "
              + "password encoder is present. Either delete the password attribute to perform a "
              + "bind to authenticate or set a password encoder.",
          authenticationProperties.getPasswordAttribute()));
    }
  }

  /**
   * Changes the password of the user. Extended Operation(1.3.6.1.4.1.4203.1.11.1) must be
   * supported.
   *
   * @param username the username
   * @param currentRawPassword the current password
   * @param newRawPassword the new password
   */
  public void changePassword(String username, String currentRawPassword, String newRawPassword) {
    ConnectionFactory connectionFactory = getConnectionFactory(username, currentRawPassword);
    String dn = authenticate(username, currentRawPassword, connectionFactory).getDn();
    LdaptiveTemplate ldaptiveTemplate = new LdaptiveTemplate(connectionFactory);
    ldaptiveTemplate.modifyUserPassword(dn, currentRawPassword, newRawPassword);
  }

  @Override
  public LdaptiveAuthenticationToken authenticate(Authentication authentication)
      throws AuthenticationException {
    String username = authentication.getName();
    String password = String.valueOf(authentication.getCredentials());
    ConnectionFactory connectionFactory = getConnectionFactory(username, password);
    return authenticate(username, password, connectionFactory);
  }

  protected LdaptiveAuthenticationToken authenticate(
      String username,
      String password,
      ConnectionFactory connectionFactory) {
    try {
      LdaptiveTemplate ldaptiveTemplate = new LdaptiveTemplate(connectionFactory);
      LdapEntry user = getUser(ldaptiveTemplate, username);
      checkPassword(ldaptiveTemplate, user, password);
      checkAccountControl(user);
      Collection<? extends GrantedAuthority> roles = getAllRoles(ldaptiveTemplate, user);
      return new LdaptiveAuthenticationToken(
          user,
          LdaptiveEntryMapper.getAttributeValue(
              user, authenticationProperties.getUsernameAttribute(), STRING_TRANSCODER, username),
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
    if (bindWithAuthentication()) {
      ConnectionConfig authConfig = ConnectionConfig.copy(this.connectionConfig);
      String bindDn = usernameToBindDnConverter.convert(username);
      authConfig.setConnectionInitializers(BindConnectionInitializer.builder()
          .dn(bindDn)
          .credential(password)
          .build());
      return new DefaultConnectionFactory(authConfig);
    }
    return new DefaultConnectionFactory(connectionConfig);
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
    if (isInvalidCredentialsException(exception.getLdapException())) {
      return badCredentials;
    }
    return exception;
  }

  private boolean isInvalidCredentialsException(LdapException exception) {
    if (Objects.isNull(exception)) {
      return false;
    }
    if (ResultCode.INVALID_CREDENTIALS.equals(exception.getResultCode())) {
      return true;
    }
    String message = Optional.ofNullable(exception.getMessage())
        .map(String::toLowerCase)
        .orElse("");
    String text = ("resultCode=" + ResultCode.INVALID_CREDENTIALS).toLowerCase();
    if (ResultCode.CONNECT_ERROR.equals(exception.getResultCode()) && message.contains(text)) {
      return true;
    }
    if (exception.getCause() instanceof LdapException cause) {
      return isInvalidCredentialsException(cause);
    }
    return false;
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

    if (!bindWithAuthentication()) {
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

    if (Boolean.FALSE.equals(authenticationProperties.getLdapGroupsToRolesMappingEnabled())) {
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
   * To granted authority.
   *
   * @param groupName the group name
   * @return the granted authority
   */
  protected GrantedAuthority toGrantedAuthority(String groupName) {
    return new SimpleGrantedAuthority(mapGroupToRole(groupName));
  }

  /**
   * Map group to role.
   *
   * @param groupName the group name
   * @return the role name
   */
  protected String mapGroupToRole(String groupName) {
    String roleName = Stream.ofNullable(authenticationProperties.getGroupToRoleMapping())
        .flatMap(Collection::stream)
        .filter(mapping -> groupName.equalsIgnoreCase(mapping.getGroupName()))
        .map(GroupToRoleMapping::getRoleName)
        .filter(role -> !isEmpty(role))
        .filter(role -> !role.isBlank())
        .findFirst()
        .orElse(groupName);
    return normalizeRole(roleName);
  }

  /**
   * Normalize role name.
   *
   * @param roleName the role name
   * @return the normalized role name
   */
  protected String normalizeRole(String roleName) {
    String normalizedRoleName = roleName;
    if (!isEmpty(authenticationProperties.getRoleStringReplacements())) {
      for (StringReplacement replacement : authenticationProperties.getRoleStringReplacements()) {
        normalizedRoleName = normalizedRoleName
            .replaceAll(replacement.getRegex(), replacement.getReplacement());
      }
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
        user, authenticationProperties.getUsernameAttribute(), STRING_TRANSCODER, null);
  }

}
