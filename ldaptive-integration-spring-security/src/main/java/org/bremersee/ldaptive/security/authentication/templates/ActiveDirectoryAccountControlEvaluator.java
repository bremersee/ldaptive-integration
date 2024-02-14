package org.bremersee.ldaptive.security.authentication.templates;

import static org.bremersee.ldaptive.LdaptiveEntryMapper.getAttributeValue;

import org.bremersee.ldaptive.security.authentication.AccountControlEvaluator;
import org.bremersee.ldaptive.transcoder.UserAccountControlValueTranscoder;
import org.ldaptive.LdapEntry;

public class ActiveDirectoryAccountControlEvaluator implements AccountControlEvaluator {

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
    Integer value = getAttributeValue(
        ldapEntry,
        UserAccountControlValueTranscoder.ATTRIBUTE_NAME,
        new UserAccountControlValueTranscoder(),
        null);
    return UserAccountControlValueTranscoder.isUserAccountEnabled(value, true);
  }
}
