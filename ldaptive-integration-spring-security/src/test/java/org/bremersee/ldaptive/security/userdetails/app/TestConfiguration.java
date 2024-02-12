/*
 * Copyright 2021 the original author or authors.
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

package org.bremersee.ldaptive.security.userdetails.app;

import org.bremersee.ldaptive.LdaptiveTemplate;
import org.bremersee.ldaptive.reactive.ReactiveLdaptiveTemplate;
import org.ldaptive.BindConnectionInitializer;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.DefaultConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * The test configuration to test the ldaptive security.
 *
 * @author Christian Bremer
 */
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = {TestConfiguration.class})
public class TestConfiguration {

  @Value("${spring.ldap.embedded.credential.username}")
  private String username;

  @Value("${spring.ldap.embedded.credential.password}")
  private String password;

  @Value("${spring.ldap.embedded.port}")
  private int port;

  /**
   * Connection factory.
   *
   * @return the connection factory
   */
  @Bean
  public ConnectionFactory connectionFactory() {
    return DefaultConnectionFactory.builder()
        .config(ConnectionConfig.builder()
            .url("ldap://localhost:" + port)
            .connectionInitializers(BindConnectionInitializer.builder()
                .dn(username)
                .credential(password)
                .build())
            .build())
        .build();
  }

  /**
   * Ldaptive template.
   *
   * @return the ldaptive template
   */
  @Bean
  public LdaptiveTemplate ldaptiveTemplate() {
    return new LdaptiveTemplate(connectionFactory());
  }

  /**
   * Reactive ldaptive template.
   *
   * @return the reactive ldaptive template
   */
  @Bean
  public ReactiveLdaptiveTemplate reactiveLdaptiveTemplate() {
    return new ReactiveLdaptiveTemplate(connectionFactory());
  }

}
