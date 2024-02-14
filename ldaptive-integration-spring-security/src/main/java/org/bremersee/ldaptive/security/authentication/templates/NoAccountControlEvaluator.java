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

package org.bremersee.ldaptive.security.authentication.templates;

import org.bremersee.ldaptive.security.authentication.AccountControlEvaluator;
import org.ldaptive.LdapEntry;

/**
 * Account is always enabled, non-locked and not expired.
 *
 * @author Christian Bremer
 */
public class NoAccountControlEvaluator implements AccountControlEvaluator {

  @Override
  public boolean isAccountNonExpired(LdapEntry ldapEntry) {
    return true;
  }

  @Override
  public boolean isAccountNonLocked(LdapEntry ldapEntry) {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired(LdapEntry ldapEntry) {
    return true;
  }

  @Override
  public boolean isEnabled(LdapEntry ldapEntry) {
    return true;
  }

}
