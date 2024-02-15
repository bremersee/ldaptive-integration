/*
 * Copyright 2024 the original author or authors.
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

import java.util.function.Function;
import org.bremersee.ldaptive.security.authentication.UsernameToBindDnConverter.ByDomainEmail;
import org.bremersee.ldaptive.security.authentication.UsernameToBindDnConverter.ByUserRdnAttribute;

/**
 * The enum UsernameToBindDnConverterReference.
 *
 * @author Christian Bremer
 */
public enum UsernameToBindDnConverterProperty
    implements Function<LdaptiveAuthenticationProperties, UsernameToBindDnConverter> {

  BY_USER_RDN_ATTRIBUTE(ByUserRdnAttribute::new),

  BY_DOMAIN_EMAIL(ByDomainEmail::new);

  private final Function<LdaptiveAuthenticationProperties, UsernameToBindDnConverter> resolverFn;

  UsernameToBindDnConverterProperty(
      Function<LdaptiveAuthenticationProperties, UsernameToBindDnConverter> resolverFn) {
    this.resolverFn = resolverFn;
  }

  @Override
  public UsernameToBindDnConverter apply(LdaptiveAuthenticationProperties properties) {
    return resolverFn.apply(properties);
  }
}
