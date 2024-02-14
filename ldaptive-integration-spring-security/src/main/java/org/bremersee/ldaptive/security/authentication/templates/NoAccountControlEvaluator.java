package org.bremersee.ldaptive.security.authentication.templates;

import org.bremersee.ldaptive.security.authentication.AccountControlEvaluator;
import org.ldaptive.LdapEntry;

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
