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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.bremersee.ldaptive.LdaptiveProperties.ConnectionStrategy;
import org.bremersee.ldaptive.LdaptiveProperties.ConnectionValidatorProperties;
import org.bremersee.ldaptive.LdaptiveProperties.ConnectionValidatorProperties.SearchRequestProperties;
import org.bremersee.ldaptive.LdaptiveProperties.ConnectionValidatorProperties.SearchRequestProperties.SearchFilterProperties;
import org.bremersee.ldaptive.LdaptiveProperties.ReconnectStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.DefaultConnectionFactory;
import org.ldaptive.DnsSrvConnectionStrategy;
import org.ldaptive.RandomConnectionStrategy;
import org.ldaptive.RoundRobinConnectionStrategy;
import org.ldaptive.SearchScope;

/**
 * The type Ldaptive properties test.
 *
 * @author Christian Bremer
 */
class LdaptivePropertiesTest {

  /**
   * Is immutable.
   *
   * @param immutable the immutable
   */
  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void isImmutable(boolean immutable) {
    LdaptiveProperties target = new LdaptiveProperties();
    target.setImmutable(immutable);
    assertEquals(immutable, target.isImmutable());

    ConnectionConfig actual = target.createConnectionConfig();
    if (immutable) {
      assertThrowsExactly(
          IllegalStateException.class,
          () -> actual.setAutoReconnect(!target.isAutoReconnect()));
    } else {
      actual.setAutoReconnect(!target.isAutoReconnect());
      assertNotEquals(
          actual.getAutoReconnect(),
          target.createConnectionConfig().getAutoReconnect());
    }
  }

  /**
   * Gets ldap url.
   */
  @Test
  void getLdapUrl() {
    String value = UUID.randomUUID().toString();
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.setLdapUrl(value);

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.setLdapUrl(value);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains(value));
  }

  /**
   * Gets connect timeout.
   */
  @Test
  void getConnectTimeout() {
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.setConnectTimeout(Duration.ofMillis(123456789L));

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.setConnectTimeout(Duration.ofMillis(123456789L));

    assertEquals(expected, actual);

    ConnectionConfig connectionConfig = actual.createConnectionConfig();
    assertEquals(actual.getConnectTimeout(), connectionConfig.getConnectTimeout());
  }

  /**
   * Gets start tls timeout.
   */
  @Test
  void getStartTlsTimeout() {
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.setStartTlsTimeout(Duration.ofMillis(123456789L));

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.setStartTlsTimeout(Duration.ofMillis(123456789L));

    assertEquals(expected, actual);

    ConnectionConfig connectionConfig = actual.createConnectionConfig();
    assertEquals(actual.getStartTlsTimeout(), connectionConfig.getStartTLSTimeout());
  }

  /**
   * Gets response timeout.
   */
  @Test
  void getResponseTimeout() {
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.setResponseTimeout(Duration.ofMillis(123456789L));

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.setResponseTimeout(Duration.ofMillis(123456789L));

    assertEquals(expected, actual);

    ConnectionConfig connectionConfig = actual.createConnectionConfig();
    assertEquals(actual.getResponseTimeout(), connectionConfig.getResponseTimeout());
  }

  /**
   * Gets reconnect timeout.
   */
  @Test
  void getReconnectTimeout() {
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.setReconnectTimeout(Duration.ofMillis(123456789L));

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.setReconnectTimeout(Duration.ofMillis(123456789L));

    assertEquals(expected, actual);

    ConnectionConfig connectionConfig = actual.createConnectionConfig();
    assertEquals(actual.getReconnectTimeout(), connectionConfig.getReconnectTimeout());
  }

  /**
   * Gets reconnect strategy.
   *
   * @param strategy the strategy
   */
  @ParameterizedTest
  @ValueSource(strings = {
      "ONE_RECONNECT_ATTEMPT",
      "INFINITE_RECONNECT_ATTEMPTS",
      "INFINITE_RECONNECT_ATTEMPTS_WITH_BACKOFF"
  })
  void getReconnectStrategy(String strategy) {
    ReconnectStrategy reconnectStrategy = ReconnectStrategy.valueOf(strategy);
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.setReconnectStrategy(reconnectStrategy);

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.setReconnectStrategy(reconnectStrategy);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains(strategy));

    ConnectionConfig connectionConfig = actual.createConnectionConfig();
    assertNotNull(connectionConfig.getAutoReconnectCondition());
  }

  /**
   * Gets reconnect strategy infinitive.
   */
  @Test
  void getReconnectStrategyInfinitive() {
    ReconnectStrategy reconnectStrategy = ReconnectStrategy.INFINITE_RECONNECT_ATTEMPTS;
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.setReconnectStrategy(reconnectStrategy);

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.setReconnectStrategy(reconnectStrategy);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains(reconnectStrategy.name()));
  }

  /**
   * Gets reconnect strategy infinitive with backoff.
   */
  @Test
  void getReconnectStrategyInfinitiveWithBackoff() {
    ReconnectStrategy reconnectStrategy = ReconnectStrategy
        .INFINITE_RECONNECT_ATTEMPTS_WITH_BACKOFF;
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.setReconnectStrategy(reconnectStrategy);

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.setReconnectStrategy(reconnectStrategy);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains(reconnectStrategy.name()));
  }

  /**
   * Gets connection validator.
   */
  @Test
  void getConnectionValidator() {
    LdaptiveProperties target = new LdaptiveProperties();
    ConnectionValidatorProperties expected = new ConnectionValidatorProperties();
    target.setConnectionValidator(expected);
    expected.setValidateTimeout(Duration.ofMillis(123456789L));
    expected.setValidatePeriod(Duration.ofMillis(54321));
    SearchRequestProperties searchRequest = new SearchRequestProperties();
    expected.setSearchRequest(searchRequest);
    searchRequest.setBaseDn("cn=users,dc=ad,dc=example,dc=org");
    SearchFilterProperties filter = new SearchFilterProperties();
    filter.setFilter("(uid=administrator)");
    searchRequest.setSearchFilter(filter);
    searchRequest.setSearchScope(SearchScope.ONELEVEL);
    searchRequest.setSizeLimit(1);
    searchRequest.setReturnAttributes(List.of("cn"));

    assertEquals(expected, target.getConnectionValidator());

    ConnectionConfig connectionConfig = target.createConnectionConfig();
    assertNotNull(connectionConfig.getConnectionValidator());
  }

  /**
   * Gets connection validator without attributes.
   */
  @Test
  void getConnectionValidatorWithoutAttributes() {
    LdaptiveProperties target = new LdaptiveProperties();
    ConnectionValidatorProperties expected = new ConnectionValidatorProperties();
    target.setConnectionValidator(expected);
    expected.setValidateTimeout(Duration.ofMillis(456789L));
    expected.setValidatePeriod(Duration.ofMillis(7654321));
    SearchRequestProperties searchRequest = new SearchRequestProperties();
    expected.setSearchRequest(searchRequest);
    searchRequest.setBaseDn("cn=users,dc=ad,dc=example,dc=org");
    SearchFilterProperties filter = new SearchFilterProperties();
    filter.setFilter("(uid=administrator)");
    searchRequest.setSearchFilter(filter);
    searchRequest.setSearchScope(SearchScope.ONELEVEL);
    searchRequest.setSizeLimit(1);

    assertEquals(expected, target.getConnectionValidator());

    ConnectionConfig connectionConfig = target.createConnectionConfig();
    assertNotNull(connectionConfig.getConnectionValidator());
  }

  /**
   * Gets auto replay.
   *
   * @param value the value
   */
  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getAutoReplay(boolean value) {
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.setAutoReplay(value);
    assertEquals(value, expected.isAutoReplay());

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.setAutoReplay(value);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains("" + value));

    ConnectionConfig connectionConfig = actual.createConnectionConfig();
    assertEquals(actual.isAutoReplay(), connectionConfig.getAutoReplay());
  }

  /**
   * Is use start tls.
   *
   * @param value the value
   */
  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void isUseStartTls(boolean value) {
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.setUseStartTls(value);
    assertEquals(value, expected.isUseStartTls());

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.setUseStartTls(value);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains("" + value));

    ConnectionConfig connectionConfig = actual.createConnectionConfig();
    assertEquals(actual.isUseStartTls(), connectionConfig.getUseStartTLS());
  }

  /**
   * Gets trust certificates.
   */
  @Test
  void getTrustCertificates() {
    String value = UUID.randomUUID().toString();
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.getSslConfig().setTrustCertificates(value);

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.getSslConfig().setTrustCertificates(value);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains(value));
  }

  /**
   * Gets authentication certificate.
   */
  @Test
  void getAuthenticationCertificate() {
    String value = UUID.randomUUID().toString();
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.getSslConfig().setAuthenticationCertificate(value);

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.getSslConfig().setAuthenticationCertificate(value);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains(value));
  }

  /**
   * Gets authentication key.
   */
  @Test
  void getAuthenticationKey() {
    String value = UUID.randomUUID().toString();
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.getSslConfig().setAuthenticationKey(value);

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.getSslConfig().setAuthenticationKey(value);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains(value));
  }

  /**
   * Gets bind dn.
   */
  @Test
  void getBindDn() {
    String value = UUID.randomUUID().toString();
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.setBindDn(value);

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.setBindDn(value);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains(value));

    actual.setBindCredentials("test");
    ConnectionConfig connectionConfig = actual.createConnectionConfig();
    assertEquals(1, connectionConfig.getConnectionInitializers().length);
  }

  /**
   * Gets bind credential.
   */
  @Test
  void getBindCredential() {
    String value = UUID.randomUUID().toString();
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.setBindCredentials(value);
    assertEquals(value, expected.getBindCredentials());
  }

  /**
   * Is fast bind.
   *
   * @param value the value
   */
  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void isFastBind(boolean value) {
    LdaptiveProperties target = new LdaptiveProperties();
    target.setFastBind(value);

    assertEquals(value, target.isFastBind());

    ConnectionConfig connectionConfig = target.createConnectionConfig();
    if (value) {
      assertEquals(1, connectionConfig.getConnectionInitializers().length);
    } else {
      assertEquals(0, connectionConfig.getConnectionInitializers().length);
    }
  }

  /**
   * Gets connection strategy.
   *
   * @param strategy the strategy
   */
  @ParameterizedTest
  @ValueSource(strings = {
      "ACTIVE_PASSIVE",
      "RANDOM",
      "ROUND_ROBIN",
      "DNS"
  })
  void getConnectionStrategy(String strategy) {
    ConnectionStrategy connectionStrategy = ConnectionStrategy.valueOf(strategy);
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.setConnectionStrategy(connectionStrategy);

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.setConnectionStrategy(connectionStrategy);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains(strategy));

    ConnectionConfig connectionConfig = actual.createConnectionConfig();
    assertTrue(connectionStrategy.get().getClass()
        .isAssignableFrom(connectionConfig.getConnectionStrategy().getClass()));
  }

  /**
   * Gets connection strategy random.
   */
  @Test
  void getConnectionStrategyRandom() {
    ConnectionStrategy connectionStrategy = ConnectionStrategy.RANDOM;
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.setConnectionStrategy(connectionStrategy);

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.setConnectionStrategy(connectionStrategy);

    ConnectionConfig connectionConfig = actual.createConnectionConfig();
    assertTrue(RandomConnectionStrategy.class
        .isAssignableFrom(connectionConfig.getConnectionStrategy().getClass()));
  }

  /**
   * Gets connection strategy round robin.
   */
  @Test
  void getConnectionStrategyRoundRobin() {
    ConnectionStrategy connectionStrategy = ConnectionStrategy.ROUND_ROBIN;
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.setConnectionStrategy(connectionStrategy);

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.setConnectionStrategy(connectionStrategy);

    ConnectionConfig connectionConfig = actual.createConnectionConfig();
    assertTrue(RoundRobinConnectionStrategy.class
        .isAssignableFrom(connectionConfig.getConnectionStrategy().getClass()));
  }

  /**
   * Gets connection strategy dns.
   */
  @Test
  void getConnectionStrategyDns() {
    ConnectionStrategy connectionStrategy = ConnectionStrategy.DNS;
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.setConnectionStrategy(connectionStrategy);

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.setConnectionStrategy(connectionStrategy);

    ConnectionConfig connectionConfig = actual.createConnectionConfig();
    assertTrue(DnsSrvConnectionStrategy.class
        .isAssignableFrom(connectionConfig.getConnectionStrategy().getClass()));
  }

  /**
   * Is pooled.
   *
   * @param value the value
   */
  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void isPooled(boolean value) {
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.setPooled(value);
    assertEquals(value, expected.isPooled());

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.setPooled(value);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains("" + value));

    if (value) {
      assertThrowsExactly(IllegalStateException.class, actual::createConnectionFactory);
    } else {
      assertInstanceOf(DefaultConnectionFactory.class, actual.createConnectionFactory());
    }
  }

  /**
   * Gets min pool size.
   */
  @Test
  void getMinPoolSize() {
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.getConnectionPool().setMinPoolSize(1234567);

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.getConnectionPool().setMinPoolSize(1234567);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains("1234567"));
  }

  /**
   * Gets max pool size.
   */
  @Test
  void getMaxPoolSize() {
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.getConnectionPool().setMaxPoolSize(1234567);

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.getConnectionPool().setMaxPoolSize(1234567);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains("1234567"));
  }

  /**
   * Is validate on check in.
   */
  @Test
  void isValidateOnCheckIn() {
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.getConnectionPool().setValidateOnCheckIn(true);
    assertTrue(expected.getConnectionPool().isValidateOnCheckIn());

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.getConnectionPool().setValidateOnCheckIn(true);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains("true"));
  }

  /**
   * Is validate on check out.
   */
  @Test
  void isValidateOnCheckOut() {
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.getConnectionPool().setValidateOnCheckOut(true);
    assertTrue(expected.getConnectionPool().isValidateOnCheckOut());

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.getConnectionPool().setValidateOnCheckOut(true);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains("true"));
  }

  /**
   * Is validate periodically.
   */
  @Test
  void isValidatePeriodically() {
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.getConnectionPool().setValidatePeriodically(true);
    assertTrue(expected.getConnectionPool().isValidatePeriodically());

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.getConnectionPool().setValidatePeriodically(true);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains("true"));
  }

  /**
   * Gets validate period.
   */
  @Test
  void getValidatePeriod() {
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.getConnectionPool().getValidator().setValidatePeriod(Duration.ofMillis(123456789L));

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.getConnectionPool().getValidator().setValidatePeriod(Duration.ofMillis(123456789L));

    assertEquals(expected, actual);
  }

  /**
   * Gets prune period.
   */
  @Test
  void getPrunePeriod() {
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.getConnectionPool().setPrunePeriod(Duration.ofMillis(123456789L));

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.getConnectionPool().setPrunePeriod(Duration.ofMillis(123456789L));

    assertEquals(expected, actual);
  }

  /**
   * Gets idle time.
   */
  @Test
  void getIdleTime() {
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.getConnectionPool().setIdleTime(Duration.ofMillis(123456789L));

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.getConnectionPool().setIdleTime(Duration.ofMillis(123456789L));

    assertEquals(expected, actual);
  }

  /**
   * Gets block wait time.
   */
  @Test
  void getBlockWaitTime() {
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.getConnectionPool().setBlockWaitTime(Duration.ofMillis(123456789L));

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.getConnectionPool().setBlockWaitTime(Duration.ofMillis(123456789L));

    assertEquals(expected, actual);
  }

  /**
   * Gets search validator.
   */
  @Test
  void getSearchValidator() {
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.getConnectionPool().setValidator(new ConnectionValidatorProperties());
    assertNotNull(expected.getConnectionPool().getValidator());
  }

}