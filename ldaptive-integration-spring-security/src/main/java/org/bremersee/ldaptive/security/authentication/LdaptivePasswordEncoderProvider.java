package org.bremersee.ldaptive.security.authentication;

import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface LdaptivePasswordEncoderProvider {

  PasswordEncoder getPasswordEncoder();

  class DefaultLdaptivePasswordEncoderProvider implements LdaptivePasswordEncoderProvider {

    @Override
    public PasswordEncoder getPasswordEncoder() {
      //noinspection deprecation
      return new LdapShaPasswordEncoder(KeyGenerators.shared(0));
    }
  }

}
