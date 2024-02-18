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
import java.util.UUID;
import org.bremersee.ldaptive.LdaptiveProperties.ConnectionValidatorProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.DefaultConnectionFactory;

/**
 * The type Ldaptive properties test.
 *
 * @author Christian Bremer
 */
class LdaptivePropertiesTest {

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

  @Test
  void getBindCredential() {
    String value = UUID.randomUUID().toString();
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.setBindCredentials(value);
    assertEquals(value, expected.getBindCredentials());
  }

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

  @Test
  void getMinPoolSize() {
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.getConnectionPool().setMinPoolSize(1234567);

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.getConnectionPool().setMinPoolSize(1234567);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains("1234567"));
  }

  @Test
  void getMaxPoolSize() {
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.getConnectionPool().setMaxPoolSize(1234567);

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.getConnectionPool().setMaxPoolSize(1234567);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains("1234567"));
  }

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

  @Test
  void getValidatePeriod() {
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.getConnectionPool().getValidator().setValidatePeriod(Duration.ofMillis(123456789L));

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.getConnectionPool().getValidator().setValidatePeriod(Duration.ofMillis(123456789L));

    assertEquals(expected, actual);
  }

  @Test
  void getPrunePeriod() {
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.getConnectionPool().setPrunePeriod(Duration.ofMillis(123456789L));

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.getConnectionPool().setPrunePeriod(Duration.ofMillis(123456789L));

    assertEquals(expected, actual);
  }

  @Test
  void getIdleTime() {
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.getConnectionPool().setIdleTime(Duration.ofMillis(123456789L));

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.getConnectionPool().setIdleTime(Duration.ofMillis(123456789L));

    assertEquals(expected, actual);
  }

  @Test
  void getBlockWaitTime() {
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.getConnectionPool().setBlockWaitTime(Duration.ofMillis(123456789L));

    LdaptiveProperties actual = new LdaptiveProperties();
    actual.getConnectionPool().setBlockWaitTime(Duration.ofMillis(123456789L));

    assertEquals(expected, actual);
  }

  @Test
  void getSearchValidator() {
    LdaptiveProperties expected = new LdaptiveProperties();
    expected.getConnectionPool().setValidator(new ConnectionValidatorProperties());
    assertNotNull(expected.getConnectionPool().getValidator());
  }

}