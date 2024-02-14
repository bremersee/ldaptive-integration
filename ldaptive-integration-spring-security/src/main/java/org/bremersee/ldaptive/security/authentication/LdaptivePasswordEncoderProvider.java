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

import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * The interface Ldaptive password encoder provider.
 *
 * @author Christian Bremer
 */
public interface LdaptivePasswordEncoderProvider {

  /**
   * Gets password encoder.
   *
   * @return the password encoder
   */
  PasswordEncoder getPasswordEncoder();

  /**
   * The type Default ldaptive password encoder provider.
   */
  class DefaultLdaptivePasswordEncoderProvider implements LdaptivePasswordEncoderProvider {

    @Override
    public PasswordEncoder getPasswordEncoder() {
      //noinspection deprecation
      return new LdapShaPasswordEncoder(KeyGenerators.shared(0));
    }
  }

}
