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

import java.util.Objects;
import java.util.function.Predicate;
import org.ldaptive.BindConnectionInitializer;
import org.ldaptive.ClosedRetryMetadata;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.ConnectionInitializer;
import org.ldaptive.Credential;
import org.ldaptive.RetryMetadata;
import org.ldaptive.ad.extended.FastBindConnectionInitializer;
import org.ldaptive.ssl.CredentialConfig;
import org.ldaptive.ssl.SslConfig;
import org.ldaptive.ssl.X509CredentialConfig;

/**
 * The interface Ldaptive connection config provider.
 *
 * @author Christian Bremer
 */
public interface LdaptiveConnectionConfigProvider {

  /**
   * Gets connection config.
   *
   * @return the connection config
   */
  ConnectionConfig getConnectionConfig();

  /**
   * Gets connection config.
   *
   * @param bindDn the bind dn
   * @param bindCredentials the bind credentials
   * @return the connection config
   */
  ConnectionConfig getConnectionConfig(String bindDn, String bindCredentials);

  /**
   * Instantiates a new default ldaptive connection config provider.
   *
   * @param properties the properties
   * @return the ldaptive connection config provider
   */
  static LdaptiveConnectionConfigProvider defaultProvider(LdaptiveProperties properties) {
    return new DefaultLdaptiveConnectionConfigProvider(properties);
  }

  /**
   * The default ldaptive connection config provider.
   */
  class DefaultLdaptiveConnectionConfigProvider implements LdaptiveConnectionConfigProvider {

    private final LdaptiveProperties properties;

    /**
     * Instantiates a new default ldaptive connection config provider.
     *
     * @param properties the properties
     */
    protected DefaultLdaptiveConnectionConfigProvider(LdaptiveProperties properties) {
      this.properties = properties;
    }

    @Override
    public ConnectionConfig getConnectionConfig() {
      return getConnectionConfig(properties.getBindDn(), properties.getBindCredentials());
    }

    @Override
    public ConnectionConfig getConnectionConfig(String bindDn, String bindCredentials) {
      return ConnectionConfig.builder()
          .autoReconnect(properties.isAutoReconnect())
          .autoReconnectCondition(autoReconnectCondition(properties))
          .autoReplay(properties.isAutoReplay())
          .connectionInitializers(connectionInitializers(bindDn, bindCredentials))
          .connectTimeout(properties.getConnectTimeout())
          .reconnectTimeout(properties.getReconnectTimeout())
          .responseTimeout(properties.getResponseTimeout())
          .sslConfig(sslConfig())
          .url(properties.getLdapUrl())
          .useStartTLS(properties.isUseStartTls())
          .build();
    }

    /**
     * Creates auto reconnect condition.
     *
     * @param properties the properties
     * @return the auto reconnect condition
     */
    protected Predicate<RetryMetadata> autoReconnectCondition(
        LdaptiveProperties properties) {
      return metadata -> {
        if (properties.getReconnectAttempts() > 0 && metadata instanceof ClosedRetryMetadata) {
          if (metadata.getAttempts() > properties.getReconnectAttempts()) {
            return false;
          }
          if (metadata.getAttempts() > 0) {
            try {
              long delay = Math.abs(properties.getReconnectBackoffDelay().toMillis());
              double multiplier = Math.abs(
                  properties.getReconnectBackoffMultiplier() * metadata.getAttempts());
              int attempts = metadata.getAttempts();
              long millis = Math.round(delay * multiplier * attempts);
              Thread.sleep(millis);
            } catch (InterruptedException e) {
              // nothing to do
            }
          }
          return true;
        }
        return false;
      };
    }

    /**
     * Creates connection initializers.
     *
     * @param bindDn the bind dn
     * @param bindCredentials the bind credentials
     * @return the connection initializers
     */
    protected ConnectionInitializer[] connectionInitializers(
        String bindDn,
        String bindCredentials) {

      if (hasText(bindDn) && hasText(bindCredentials)) {
        return new ConnectionInitializer[]{
            connectionInitializer(bindDn, bindCredentials)
        };
      } else if (properties.isFastBind()) {
        return new ConnectionInitializer[]{
            new FastBindConnectionInitializer()
        };
      }
      return new ConnectionInitializer[]{};
    }

    private ConnectionInitializer connectionInitializer(String bindDn, String bindCredential) {
      BindConnectionInitializer bci = new BindConnectionInitializer();
      bci.setBindDn(bindDn);
      bci.setBindCredential(new Credential(bindCredential));
      return bci;
    }

    /**
     * Creates ssl config.
     *
     * @return the ssl config
     */
    protected SslConfig sslConfig() {
      if (hasSslConfig()) {
        SslConfig sc = new SslConfig();
        sc.setCredentialConfig(sslCredentialConfig());
        return sc;
      }
      return null;
    }

    private boolean hasSslConfig() {
      return hasText(properties.getTrustCertificates())
          || hasText(properties.getAuthenticationCertificate())
          || hasText(properties.getAuthenticationKey());
    }

    private CredentialConfig sslCredentialConfig() {
      X509CredentialConfig x509 = new X509CredentialConfig();
      if (hasText(properties.getAuthenticationCertificate())) {
        x509.setAuthenticationCertificate(properties.getAuthenticationCertificate());
      }
      if (hasText(properties.getAuthenticationKey())) {
        x509.setAuthenticationKey(properties.getAuthenticationKey());
      }
      if (hasText(properties.getTrustCertificates())) {
        x509.setTrustCertificates(properties.getTrustCertificates());
      }
      return x509;
    }

    /**
     * Has text boolean.
     *
     * @param value the value
     * @return the boolean
     */
    protected static boolean hasText(String value) {
      return Objects.nonNull(value) && !value.isBlank();
    }
  }

}
