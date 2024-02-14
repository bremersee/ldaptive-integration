/*
 * Copyright 2021-2022 the original author or authors.
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

package org.bremersee.ldaptive.spring.boot.autoconfigure;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * The ldaptive autoconfiguration test.
 */
class LdaptiveAutoConfigurationTest {

  /**
   * Init.
   */
  @Test
  void init() {
    LdaptiveAutoConfigurationProperties properties = new LdaptiveAutoConfigurationProperties();
    properties.getConfig().setPooled(false);
    LdaptiveAutoConfiguration configuration = new LdaptiveAutoConfiguration(properties);
    assertDoesNotThrow(configuration::init);
  }

  @Test
  void connectionConfigProvider() {
    LdaptiveAutoConfigurationProperties properties = new LdaptiveAutoConfigurationProperties();
    properties.getConfig().setPooled(false);
    LdaptiveAutoConfiguration configuration = new LdaptiveAutoConfiguration(properties);
    assertNotNull(configuration.connectionConfigProvider());
  }

  @Test
  void connectionFactoryProvider() {
    LdaptiveAutoConfigurationProperties properties = new LdaptiveAutoConfigurationProperties();
    properties.getConfig().setPooled(false);
    LdaptiveAutoConfiguration configuration = new LdaptiveAutoConfiguration(properties);
    assertNotNull(configuration.connectionFactoryProvider());
  }

  /**
   * Connection factory.
   */
  @Test
  void connectionFactory() {
    LdaptiveAutoConfigurationProperties properties = new LdaptiveAutoConfigurationProperties();
    properties.getConfig().setPooled(false);

    LdaptiveAutoConfiguration configuration = new LdaptiveAutoConfiguration(properties);
    assertNotNull(configuration.connectionFactory(
        configuration.connectionConfigProvider(),
        configuration.connectionFactoryProvider()));
  }

  /**
   * Ldaptive template.
   */
  @Test
  void ldaptiveTemplate() {
    LdaptiveAutoConfigurationProperties properties = new LdaptiveAutoConfigurationProperties();
    properties.getConfig().setPooled(false);

    LdaptiveAutoConfiguration configuration = new LdaptiveAutoConfiguration(properties);
    assertNotNull(configuration.ldaptiveTemplate(configuration.connectionFactory(
        configuration.connectionConfigProvider(),
        configuration.connectionFactoryProvider())));
  }

  @Test
  void reactiveLdaptiveTemplate() {
    LdaptiveAutoConfigurationProperties properties = new LdaptiveAutoConfigurationProperties();
    properties.getConfig().setPooled(false);

    LdaptiveAutoConfiguration configuration = new LdaptiveAutoConfiguration(properties);
    assertNotNull(configuration.reactiveLdaptiveTemplate(configuration.connectionFactory(
        configuration.connectionConfigProvider(),
        configuration.connectionFactoryProvider())));
  }

}