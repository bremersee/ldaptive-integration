package org.bremersee.ldaptive.security.authentication;

import org.springframework.security.authentication.ReactiveAuthenticationManagerAdapter;

public class ReactiveLdaptiveAuthenticationManager extends ReactiveAuthenticationManagerAdapter {

  public ReactiveLdaptiveAuthenticationManager(
      LdaptiveAuthenticationManager authenticationManager) {
    super(authenticationManager);
  }

}
