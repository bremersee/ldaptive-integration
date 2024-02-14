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

public interface LdaptiveConnectionConfigProvider {

  ConnectionConfig getConnectionConfig();

  ConnectionConfig getConnectionConfig(String bindDn, String bindCredentials);

  class DefaultLdaptiveConnectionConfigProvider implements LdaptiveConnectionConfigProvider {

    private final LdaptiveProperties properties;

    public DefaultLdaptiveConnectionConfigProvider(LdaptiveProperties properties) {
      this.properties = properties;
    }

    @Override
    public ConnectionConfig getConnectionConfig() {
      return getConnectionConfig(properties.getBindDn(), properties.getBindCredentials());
    }

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

    private Predicate<RetryMetadata> autoReconnectCondition(
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

    private ConnectionInitializer[] connectionInitializers(
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

    private SslConfig sslConfig() {
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

    private static boolean hasText(String value) {
      return Objects.nonNull(value) && !value.isBlank();
    }
  }

}
