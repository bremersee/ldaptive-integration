/*
 * Copyright 2019 the original author or authors.
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

import lombok.extern.slf4j.Slf4j;
import org.bremersee.exception.ServiceException;
import org.bremersee.ldaptive.LdaptiveConnectionConfigProvider;
import org.bremersee.ldaptive.LdaptiveConnectionConfigProvider.DefaultLdaptiveConnectionConfigProvider;
import org.bremersee.ldaptive.LdaptiveConnectionFactoryProvider;
import org.bremersee.ldaptive.LdaptiveConnectionFactoryProvider.DefaultLdaptiveConnectionFactoryProvider;
import org.bremersee.ldaptive.LdaptiveOperations;
import org.bremersee.ldaptive.LdaptiveProperties;
import org.bremersee.ldaptive.LdaptiveTemplate;
import org.bremersee.ldaptive.reactive.ReactiveLdaptiveOperations;
import org.bremersee.ldaptive.reactive.ReactiveLdaptiveTemplate;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchResponse;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.event.EventListener;
import org.springframework.util.ClassUtils;

/**
 * The ldaptive configuration.
 *
 * @author Christian Bremer
 */
@AutoConfiguration
@ConditionalOnClass(name = {
    "org.ldaptive.ConnectionFactory",
    "org.bremersee.ldaptive.LdaptiveTemplate"
})
@ConditionalOnProperty(prefix = "bremersee.ldaptive", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(LdaptiveAutoConfigurationProperties.class)
@Slf4j
public class LdaptiveAutoConfiguration {

  private final LdaptiveProperties properties;

  /**
   * Instantiates a new ldaptive configuration.
   *
   * @param ldaptiveProperties the ldaptive properties
   */
  public LdaptiveAutoConfiguration(LdaptiveAutoConfigurationProperties ldaptiveProperties) {
    this.properties = ldaptiveProperties.getConfig();
  }

  /**
   * Init.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void init() {
    log.info("""

            *********************************************************************************
            * {}
            * properties = {}
            *********************************************************************************""",
        ClassUtils.getUserClass(getClass()).getSimpleName(),
        properties);
  }


  @ConditionalOnMissingBean(LdaptiveConnectionConfigProvider.class)
  @Bean
  public LdaptiveConnectionConfigProvider connectionConfigProvider() {
    return new DefaultLdaptiveConnectionConfigProvider(properties);
  }

  @ConditionalOnMissingBean(LdaptiveConnectionFactoryProvider.class)
  @Bean
  public LdaptiveConnectionFactoryProvider connectionFactoryProvider() {
    return new DefaultLdaptiveConnectionFactoryProvider(properties);
  }

  /**
   * Creates connection factory bean.
   *
   * @return the connection factory bean
   */
  @ConditionalOnMissingBean(ConnectionFactory.class)
  @Bean(destroyMethod = "close")
  public ConnectionFactory connectionFactory(
      LdaptiveConnectionConfigProvider connectionConfigProvider,
      LdaptiveConnectionFactoryProvider connectionFactoryProvider) {

    ConnectionConfig connectionConfig = connectionConfigProvider.getConnectionConfig();
    if (properties.isPooled()) {
      ConnectionFactory factory = connectionFactoryProvider
          .getDefaultConnectionFactory(connectionConfig);
      validatePooledConnection(factory);
      return connectionFactoryProvider.getPooledConnectionFactory(connectionConfig);
    }
    return connectionFactoryProvider.getDefaultConnectionFactory(connectionConfig);
  }

  private void validatePooledConnection(ConnectionFactory factory) {
    try {
      log.info("Checking validation properties {}", properties.getSearchValidator());
      LdaptiveTemplate ldaptiveTemplate = new LdaptiveTemplate(factory);
      SearchRequest request = properties.getSearchValidator().getSearchRequest()
          .createSearchRequest();
      SearchResponse response = ldaptiveTemplate.search(request);
      if (!response.isSuccess()) {
        ServiceException se = ServiceException.internalServerError(
            "Invalid search validator. There is no result executing search validator.",
            "org.bremersee:ldaptive-integration:bf2c08f6-65bf-417c-8ab9-1c069f46bde2");
        log.error("Validation of pool validation failed.", se);
        throw se;
      }
      log.info("Checking validation properties: successfully done!");

    } finally {
      factory.close();
    }
  }

  /**
   * Builds ldaptive template.
   *
   * @param connectionFactory the connection factory
   * @return the ldaptive template
   */
  @ConditionalOnMissingBean(LdaptiveOperations.class)
  @Bean
  public LdaptiveTemplate ldaptiveTemplate(ConnectionFactory connectionFactory) {
    return new LdaptiveTemplate(connectionFactory);
  }

  /**
   * Builds reactive ldaptive template.
   *
   * @param connectionFactory the connection factory
   * @return the reactive ldaptive template
   */
  @ConditionalOnClass(name = {"reactor.core.publisher.Mono"})
  @ConditionalOnMissingBean(ReactiveLdaptiveOperations.class)
  @Bean
  public ReactiveLdaptiveTemplate reactiveLdaptiveTemplate(ConnectionFactory connectionFactory) {
    return new ReactiveLdaptiveTemplate(connectionFactory);
  }

}
