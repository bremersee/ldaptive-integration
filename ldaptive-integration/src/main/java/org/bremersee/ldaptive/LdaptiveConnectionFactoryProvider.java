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

package org.bremersee.ldaptive;

import org.ldaptive.ConnectionConfig;
import org.ldaptive.DefaultConnectionFactory;
import org.ldaptive.PooledConnectionFactory;
import org.ldaptive.pool.IdlePruneStrategy;

/**
 * The interface Ldaptive connection factory provider.
 *
 * @author Christian Bremer
 */
public interface LdaptiveConnectionFactoryProvider {

  /**
   * Gets default connection factory.
   *
   * @param connectionConfig the connection config
   * @return the default connection factory
   */
  default DefaultConnectionFactory getDefaultConnectionFactory(ConnectionConfig connectionConfig) {
    return DefaultConnectionFactory.builder()
        .config(connectionConfig)
        .build();
  }

  /**
   * Gets pooled connection factory.
   *
   * @param connectionConfig the connection config
   * @return the pooled connection factory
   */
  PooledConnectionFactory getPooledConnectionFactory(ConnectionConfig connectionConfig);

  /**
   * Instantiates a new default ldaptive connection factory provider.
   *
   * @param properties the properties
   */
  static LdaptiveConnectionFactoryProvider defaultProvider(LdaptiveProperties properties) {
    return new DefaultLdaptiveConnectionFactoryProvider(properties);
  }

  /**
   * The default ldaptive connection factory provider.
   */
  class DefaultLdaptiveConnectionFactoryProvider implements LdaptiveConnectionFactoryProvider {

    private final LdaptiveProperties properties;

    /**
     * Instantiates a new default ldaptive connection factory provider.
     *
     * @param properties the properties
     */
    DefaultLdaptiveConnectionFactoryProvider(LdaptiveProperties properties) {
      this.properties = properties;
    }

    @Override
    public PooledConnectionFactory getPooledConnectionFactory(ConnectionConfig connectionConfig) {
      PooledConnectionFactory factory = PooledConnectionFactory.builder()
          .config(connectionConfig)
          .blockWaitTime(properties.getBlockWaitTime())
          .connectOnCreate(properties.isConnectOnCreate())
          .failFastInitialize(properties.isFailFastInitialize())
          .max(properties.getMaxPoolSize())
          .min(properties.getMinPoolSize())
          .pruneStrategy(
              new IdlePruneStrategy(properties.getPrunePeriod(), properties.getIdleTime()))
          .validateOnCheckIn(properties.isValidateOnCheckIn())
          .validateOnCheckOut(properties.isValidateOnCheckOut())
          .validatePeriodically(properties.isValidatePeriodically())
          .validator(properties.createSearchConnectionValidator())
          .build();
      factory.initialize();
      return factory;
    }
  }

}
