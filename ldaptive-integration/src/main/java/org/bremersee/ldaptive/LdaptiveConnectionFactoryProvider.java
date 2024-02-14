package org.bremersee.ldaptive;

import org.ldaptive.ConnectionConfig;
import org.ldaptive.DefaultConnectionFactory;
import org.ldaptive.PooledConnectionFactory;
import org.ldaptive.pool.IdlePruneStrategy;

public interface LdaptiveConnectionFactoryProvider {

  default DefaultConnectionFactory getDefaultConnectionFactory(ConnectionConfig connectionConfig) {
    return DefaultConnectionFactory.builder()
        .config(connectionConfig)
        .build();
  }

  PooledConnectionFactory getPooledConnectionFactory(ConnectionConfig connectionConfig);

  class DefaultLdaptiveConnectionFactoryProvider implements LdaptiveConnectionFactoryProvider {

    private final LdaptiveProperties properties;

    public DefaultLdaptiveConnectionFactoryProvider(LdaptiveProperties properties) {
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
