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

package org.bremersee.ldaptive;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.ldaptive.LdaptiveConnectionConfigProvider.DefaultLdaptiveConnectionConfigProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The type Ldaptive connection config provider test.
 *
 * @author Christian Bremer
 */
@ExtendWith({SoftAssertionsExtension.class})
class LdaptiveConnectionConfigProviderTest {

  /**
   * Gets connection config.
   *
   * @param softly the softly
   */
  @Test
  void getConnectionConfig(SoftAssertions softly) {
    LdaptiveProperties properties = new LdaptiveProperties();
    LdaptiveConnectionConfigProvider target = new DefaultLdaptiveConnectionConfigProvider(
        properties);

    softly
        .assertThat(target.getConnectionConfig().getConnectionInitializers())
        .isEmpty();

    properties.setFastBind(true);
    softly
        .assertThat(target.getConnectionConfig().getConnectionInitializers())
        .hasSize(1);

    properties.setFastBind(false);
    properties.setBindDn("cn=admin");
    properties.setBindCredentials("secret");
    softly
        .assertThat(target.getConnectionConfig().getConnectionInitializers())
        .hasSize(1);
  }

  /**
   * Test get connection config.
   *
   * @param softly the softly
   */
  @Test
  void testGetConnectionConfig(SoftAssertions softly) {
    LdaptiveProperties properties = new LdaptiveProperties();
    LdaptiveConnectionConfigProvider target = new DefaultLdaptiveConnectionConfigProvider(
        properties);

    softly
        .assertThat(target.getConnectionConfig(null, null)
            .getConnectionInitializers())
        .isEmpty();

    softly
        .assertThat(target.getConnectionConfig("cn=admin", "secret")
            .getConnectionInitializers())
        .hasSize(1);
  }
}