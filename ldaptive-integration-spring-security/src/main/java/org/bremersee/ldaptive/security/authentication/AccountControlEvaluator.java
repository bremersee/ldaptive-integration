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

import org.ldaptive.LdapEntry;

/**
 * The interface Account control evaluator.
 *
 * @author Christian Bremer
 */
public interface AccountControlEvaluator {

  /**
   * Indicates whether the user's account has expired. An expired account cannot be authenticated.
   *
   * @param ldapEntry the ldap entry
   * @return <code>true</code> if the user's account is valid (ie non-expired),
   *     <code>false</code> if no longer valid (ie expired)
   */
  boolean isAccountNonExpired(LdapEntry ldapEntry);

  /**
   * Indicates whether the user is locked or unlocked. A locked user cannot be authenticated.
   *
   * @param ldapEntry the ldap entry
   * @return <code>true</code> if the user is not locked, <code>false</code> otherwise
   */
  boolean isAccountNonLocked(LdapEntry ldapEntry);

  /**
   * Indicates whether the user's credentials (password) has expired. Expired credentials prevent
   * authentication.
   *
   * @param ldapEntry the ldap entry
   * @return <code>true</code> if the user's credentials are valid (ie non-expired),
   *     <code>false</code> if no longer valid (ie expired)
   */
  boolean isCredentialsNonExpired(LdapEntry ldapEntry);

  /**
   * Indicates whether the user is enabled or disabled. A disabled user cannot be authenticated.
   *
   * @param ldapEntry the ldap entry
   * @return <code>true</code> if the user is enabled, <code>false</code> otherwise
   */
  boolean isEnabled(LdapEntry ldapEntry);

}
