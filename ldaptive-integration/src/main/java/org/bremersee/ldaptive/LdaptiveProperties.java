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

package org.bremersee.ldaptive;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.ldaptive.ActivePassiveConnectionStrategy;
import org.ldaptive.BindConnectionInitializer;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.ConnectionInitializer;
import org.ldaptive.ConnectionValidator;
import org.ldaptive.DefaultConnectionFactory;
import org.ldaptive.DnsSrvConnectionStrategy;
import org.ldaptive.PooledConnectionFactory;
import org.ldaptive.RandomConnectionStrategy;
import org.ldaptive.RetryMetadata;
import org.ldaptive.ReturnAttributes;
import org.ldaptive.RoundRobinConnectionStrategy;
import org.ldaptive.SearchConnectionValidator;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchScope;
import org.ldaptive.ad.extended.FastBindConnectionInitializer;
import org.ldaptive.pool.IdlePruneStrategy;
import org.ldaptive.ssl.SslConfig;
import org.ldaptive.ssl.X509CredentialConfig;

/**
 * The ldap properties.
 *
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString(exclude = {"bindCredentials"})
@EqualsAndHashCode(exclude = {"bindCredentials"})
@NoArgsConstructor
public class LdaptiveProperties {

  /**
   * Specifies whether the connection configuration is immutable or not.
   */
  private boolean immutable;

  /**
   * URL of the LDAP server(s) separated by space. For example
   * {@code ldaps://ldap1.example.org:636 ldaps://ldap2.example.org:636}
   */
  private String ldapUrl = "ldap://localhost:12389";

  /**
   * Duration of time that connects will block.
   */
  private Duration connectTimeout = Duration.ofMinutes(1);

  /**
   * Duration of time to wait for startTLS responses.
   */
  private Duration startTlsTimeout = Duration.ofMinutes(1);

  /**
   * Duration of time to wait for responses.
   */
  private Duration responseTimeout = Duration.ofMinutes(1);

  /**
   * Duration of time that operations will block on reconnects, should generally be longer than
   * connect timeout.
   */
  private Duration reconnectTimeout = Duration.ofMinutes(2);

  /**
   * Whether to automatically reconnect to the server when a connection is lost. Default is true.
   */
  private boolean autoReconnect = true;

  /**
   * The reconnect strategy.
   */
  private ReconnectStrategy reconnectStrategy = ReconnectStrategy.ONE_RECONNECT_ATTEMPT;

  /**
   * Whether pending operations should be replayed after a reconnect. Default is true.
   */
  private boolean autoReplay = true;

  /**
   * The ssl configuration.
   */
  private SslProperties sslConfig = new SslProperties();

  /**
   * Connect to LDAP using startTLS.
   */
  private boolean useStartTls;

  /**
   * DN to bind as before performing operations.
   */
  private String bindDn;

  /**
   * Credential for the bind DN.
   */
  private String bindCredentials;

  /**
   * Perform a fast bind, if no credentials are present.
   */
  private boolean fastBind = false;

  /**
   * The connection strategy.
   */
  private ConnectionStrategy connectionStrategy = ConnectionStrategy.ACTIVE_PASSIVE;

  /**
   * The connection validator.
   */
  private ConnectionValidatorProperties connectionValidator = new ConnectionValidatorProperties();

  /**
   * Specifies whether the connection should be pooled or not. Default is {@code false}.
   */
  private boolean pooled = false;

  /**
   * The connection pool configuration.
   */
  private ConnectionPoolProperties connectionPool = new ConnectionPoolProperties();

  /**
   * Creates the connection config.
   *
   * @return the connection config
   */
  public ConnectionConfig createConnectionConfig() {
    ConnectionInitializer[] connectionInitializers;
    if (hasText(getBindDn()) && hasText(getBindCredentials())) {
      connectionInitializers = new ConnectionInitializer[]{
          BindConnectionInitializer.builder()
              .dn(getBindDn())
              .credential(getBindCredentials())
              .build()
      };
    } else if (isFastBind()) {
      connectionInitializers = new ConnectionInitializer[]{
          new FastBindConnectionInitializer()
      };
    } else {
      connectionInitializers = new ConnectionInitializer[]{};
    }
    ConnectionConfig connectionConfig = ConnectionConfig.builder()
        .connectTimeout(getConnectTimeout())
        .startTLSTimeout(getStartTlsTimeout())
        .responseTimeout(getResponseTimeout())
        .reconnectTimeout(getReconnectTimeout())
        .autoReconnect(isAutoReconnect())
        .autoReconnectCondition(getReconnectStrategy().get())
        .autoReplay(isAutoReplay())
        .sslConfig(getSslConfig().createSslConfig())
        .useStartTLS(isUseStartTls())
        .connectionInitializers(connectionInitializers)
        .connectionStrategy(getConnectionStrategy().get())
        .connectionValidator(getConnectionValidator().createConnectionValidator())
        .url(getLdapUrl())
        .build();
    if (isImmutable()) {
      connectionConfig.makeImmutable();
    }
    return connectionConfig;
  }

  /**
   * Create then connection factory.
   *
   * @return the connection factory
   */
  public ConnectionFactory createConnectionFactory() {
    if (isPooled()) {
      ConnectionPoolProperties properties = getConnectionPool();
      PooledConnectionFactory factory = PooledConnectionFactory.builder()
          .config(createConnectionConfig())
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
          .validator(properties.getValidator().createConnectionValidator())
          .build();
      factory.initialize();
      return factory;
    }
    return new DefaultConnectionFactory(createConnectionConfig());
  }


  /**
   * The reconnection strategy.
   */
  public enum ReconnectStrategy implements Supplier<Predicate<RetryMetadata>> {

    /**
     * One reconnect attempt strategy.
     */
    ONE_RECONNECT_ATTEMPT(ConnectionConfig.ONE_RECONNECT_ATTEMPT),

    /**
     * Infinite reconnect attempts strategy.
     */
    INFINITE_RECONNECT_ATTEMPTS(ConnectionConfig.INFINITE_RECONNECT_ATTEMPTS),

    /**
     * Infinite reconnect attempts with backoff strategy.
     */
    INFINITE_RECONNECT_ATTEMPTS_WITH_BACKOFF(
        ConnectionConfig.INFINITE_RECONNECT_ATTEMPTS_WITH_BACKOFF);

    private final Predicate<RetryMetadata> condition;

    ReconnectStrategy(Predicate<RetryMetadata> condition) {
      this.condition = condition;
    }

    @Override
    public Predicate<RetryMetadata> get() {
      return condition;
    }
  }

  /**
   * The ssl configuration.
   */
  @Data
  @NoArgsConstructor
  public static class SslProperties {

    /**
     * Path of the trust certificates to use for the SSL connection.
     */
    private String trustCertificates;

    /**
     * Path of the authentication certificate to use for the SSL connection.
     */
    private String authenticationCertificate;

    /**
     * Path of the key to use for the SSL connection.
     */
    private String authenticationKey;

    /**
     * Create ssl config.
     *
     * @return the ssl config
     */
    public SslConfig createSslConfig() {
      if (hasText(getTrustCertificates())
          || hasText(getAuthenticationCertificate())
          || hasText(getAuthenticationKey())) {

        X509CredentialConfig x509 = new X509CredentialConfig();
        if (hasText(getAuthenticationCertificate())) {
          x509.setAuthenticationCertificate(getAuthenticationCertificate());
        }
        if (hasText(getAuthenticationKey())) {
          x509.setAuthenticationKey(getAuthenticationKey());
        }
        if (hasText(getTrustCertificates())) {
          x509.setTrustCertificates(getTrustCertificates());
        }
        SslConfig sc = new SslConfig();
        sc.setCredentialConfig(x509);
        return sc;
      }
      return null;
    }
  }

  /**
   * The connection strategy.
   */
  public enum ConnectionStrategy implements Supplier<org.ldaptive.ConnectionStrategy> {

    /**
     * Attempt each URL in the order provided for each connection. The URLs are always tried in the
     * order in which they were provided.
     */
    ACTIVE_PASSIVE(new ActivePassiveConnectionStrategy()),

    /**
     * Attempt a random URL from a list of URLs.
     */
    RANDOM(new RandomConnectionStrategy()),

    /**
     * Attempt the next URL in the order provided for each connection. URLs are rotated regardless
     * of connection success or failure.
     */
    ROUND_ROBIN(new RoundRobinConnectionStrategy()),

    /**
     * Queries a DNS server for SRV records and uses those records to construct a list of URLs. When
     * configuring this strategy you must use your DNS server for
     * {@link LdaptiveProperties#setLdapUrl(String)} in the form {@code dns://my.server.com}.
     */
    DNS(new DnsSrvConnectionStrategy());

    private final org.ldaptive.ConnectionStrategy connectionStrategy;

    ConnectionStrategy(org.ldaptive.ConnectionStrategy connectionStrategy) {
      this.connectionStrategy = connectionStrategy;
    }

    @Override
    public org.ldaptive.ConnectionStrategy get() {
      return connectionStrategy;
    }
  }

  /**
   * The search validator properties.
   */
  @Data
  @NoArgsConstructor
  public static class ConnectionValidatorProperties {

    /**
     * Validation period.
     */
    private Duration validatePeriod = Duration.ofMinutes(30);

    /**
     * Maximum length of time a connection validation should block.
     */
    private Duration validateTimeout = Duration.ofSeconds(5);

    /**
     * The search request.
     */
    private SearchRequestProperties searchRequest = new SearchRequestProperties();

    /**
     * Create connection validator.
     *
     * @return the connection validator
     */
    public ConnectionValidator createConnectionValidator() {
      if (hasText(getSearchRequest().getBaseDn())) {
        return new SearchConnectionValidator(
            validatePeriod,
            validateTimeout,
            getSearchRequest().createSearchRequest());
      }
      return null;
    }

    /**
     * The search request properties.
     */
    @Data
    @NoArgsConstructor
    public static class SearchRequestProperties {

      /**
       * The base dn (like {@code ou=peoples,dc=example,dc=org}).
       */
      private String baseDn;

      /**
       * The search filter.
       */
      private SearchFilterProperties searchFilter = new SearchFilterProperties();

      /**
       * The size limit.
       */
      private Integer sizeLimit;

      /**
       * The search scope.
       */
      private SearchScope searchScope;

      /**
       * The return attributes.
       */
      private List<String> returnAttributes = new ArrayList<>();

      /**
       * Gets the return attributes as array.
       *
       * @return the return attributes as array
       */
      public String[] returnAttributesAsArray() {
        if (returnAttributes.isEmpty()) {
          return ReturnAttributes.NONE.value();
        }
        return returnAttributes.toArray(new String[0]);
      }

      /**
       * Create search request.
       *
       * @return the search request
       */
      public SearchRequest createSearchRequest() {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setBaseDn(Objects.requireNonNullElse(getBaseDn(), ""));
        if (Objects.nonNull(getSearchFilter().getFilter())
            && !getSearchFilter().getFilter().isEmpty()) {
          searchRequest.setFilter(getSearchFilter().getFilter());
        }
        searchRequest.setReturnAttributes(returnAttributesAsArray());
        if (getSearchScope() != null) {
          searchRequest.setSearchScope(getSearchScope());
        }
        if (getSizeLimit() != null) {
          searchRequest.setSizeLimit(getSizeLimit());
        }
        return searchRequest;
      }

      /**
       * The search filter properties.
       */
      @Data
      @NoArgsConstructor
      public static class SearchFilterProperties {

        /**
         * The search filter (like {@code (&(objectClass=inetOrgPerson)(uid=administrator))}).
         */
        private String filter;

      }
    }
  }

  /**
   * The connection pool properties.
   */
  @Data
  @NoArgsConstructor
  public static class ConnectionPoolProperties {

    /**
     * Duration to wait for an available connection.
     */
    private Duration blockWaitTime = Duration.ofMinutes(1);

    /**
     * Minimum pool size.
     */
    private int minPoolSize = 3;

    /**
     * Maximum pool size.
     */
    private int maxPoolSize = 10;

    /**
     * Whether to connect to the ldap on connection creation.
     */
    private boolean connectOnCreate = true;

    /**
     * Whether initialize should throw if pooling configuration requirements are not met.
     */
    private boolean failFastInitialize = true;

    /**
     * Whether the ldap object should be validated when returned to the pool.
     */
    private boolean validateOnCheckIn = false;

    /**
     * Whether the ldap object should be validated when given from the pool.
     */
    private boolean validateOnCheckOut = false;

    /**
     * Whether the pool should be validated periodically.
     */
    private boolean validatePeriodically = false;

    /**
     * The validator for the connections in the pool.
     */
    private ConnectionValidatorProperties validator = new ConnectionValidatorProperties();

    /**
     * Prune period.
     */
    private Duration prunePeriod = Duration.ofMinutes(5);

    /**
     * Idle time.
     */
    private Duration idleTime = Duration.ofMinutes(10);
  }

  /**
   * Determines whether the given value has text or not.
   *
   * @param value the value (can be null)
   * @return the {@code true}, if the value has text, otherwise {@code false}
   */
  protected static boolean hasText(String value) {
    return Objects.nonNull(value) && !value.isBlank();
  }
}
