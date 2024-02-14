package org.bremersee.ldaptive.security.authentication;

import org.ldaptive.LdapEntry;

public interface AccountControlEvaluator {

  /**
   * Indicates whether the user's account has expired. An expired account cannot be authenticated.
   *
   * @return <code>true</code> if the user's account is valid (ie non-expired),
   *     <code>false</code> if no longer valid (ie expired)
   */
  boolean isAccountNonExpired(LdapEntry ldapEntry);

  /**
   * Indicates whether the user is locked or unlocked. A locked user cannot be authenticated.
   *
   * @return <code>true</code> if the user is not locked, <code>false</code> otherwise
   */
  boolean isAccountNonLocked(LdapEntry ldapEntry);

  /**
   * Indicates whether the user's credentials (password) has expired. Expired credentials prevent
   * authentication.
   *
   * @return <code>true</code> if the user's credentials are valid (ie non-expired),
   *     <code>false</code> if no longer valid (ie expired)
   */
  boolean isCredentialsNonExpired(LdapEntry ldapEntry);

  /**
   * Indicates whether the user is enabled or disabled. A disabled user cannot be authenticated.
   *
   * @return <code>true</code> if the user is enabled, <code>false</code> otherwise
   */
  boolean isEnabled(LdapEntry ldapEntry);

}
