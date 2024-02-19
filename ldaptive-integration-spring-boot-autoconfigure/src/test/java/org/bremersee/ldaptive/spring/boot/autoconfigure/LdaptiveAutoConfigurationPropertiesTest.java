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

package org.bremersee.ldaptive.spring.boot.autoconfigure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.UUID;
import org.bremersee.ldaptive.LdaptiveProperties.ConnectionValidatorProperties;
import org.junit.jupiter.api.Test;

/**
 * The ldaptive properties test.
 */
class LdaptiveAutoConfigurationPropertiesTest {

  @Test
  void isEnabled() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.setEnabled(true);
    assertTrue(expected.isEnabled());

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.setEnabled(true);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains("true"));

    assertNotEquals(expected, null);
    assertNotEquals(expected, new Object());
  }

  @Test
  void getLdapUrl() {
    String value = UUID.randomUUID().toString();
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConnection().setLdapUrl(value);

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConnection().setLdapUrl(value);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains(value));
  }

  @Test
  void getConnectTimeout() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConnection().setConnectTimeout(Duration.ofMillis(123456789L));

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConnection().setConnectTimeout(Duration.ofMillis(123456789L));

    assertEquals(expected, actual);
  }

  @Test
  void getResponseTimeout() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConnection().setResponseTimeout(Duration.ofMillis(123456789L));

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConnection().setResponseTimeout(Duration.ofMillis(123456789L));

    assertEquals(expected, actual);
  }

  @Test
  void isUseStartTls() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConnection().setUseStartTls(true);
    assertTrue(expected.getConnection().isUseStartTls());

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConnection().setUseStartTls(true);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains("true"));
  }

  @Test
  void getTrustCertificates() {
    String value = UUID.randomUUID().toString();
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConnection().getSslConfig().setTrustCertificates(value);

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConnection().getSslConfig().setTrustCertificates(value);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains(value));
  }

  @Test
  void getAuthenticationCertificate() {
    String value = UUID.randomUUID().toString();
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConnection().getSslConfig().setAuthenticationCertificate(value);

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConnection().getSslConfig().setAuthenticationCertificate(value);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains(value));
  }

  @Test
  void getAuthenticationKey() {
    String value = UUID.randomUUID().toString();
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConnection().getSslConfig().setAuthenticationKey(value);

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConnection().getSslConfig().setAuthenticationKey(value);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains(value));
  }

  @Test
  void getBindDn() {
    String value = UUID.randomUUID().toString();
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConnection().setBindDn(value);

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConnection().setBindDn(value);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains(value));
  }

  @Test
  void getBindCredential() {
    String value = UUID.randomUUID().toString();
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConnection().setBindCredentials(value);
    assertEquals(value, expected.getConnection().getBindCredentials());
  }

  @Test
  void isPooled() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConnection().setPooled(true);
    assertTrue(expected.getConnection().isPooled());

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConnection().setPooled(true);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains("true"));
  }

  @Test
  void getMinPoolSize() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConnection().getConnectionPool().setMinPoolSize(1234567);

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConnection().getConnectionPool().setMinPoolSize(1234567);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains("1234567"));
  }

  @Test
  void getMaxPoolSize() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConnection().getConnectionPool().setMaxPoolSize(1234567);

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConnection().getConnectionPool().setMaxPoolSize(1234567);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains("1234567"));
  }

  @Test
  void isValidateOnCheckIn() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConnection().getConnectionPool().setValidateOnCheckIn(true);
    assertTrue(expected.getConnection().getConnectionPool().isValidateOnCheckIn());

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConnection().getConnectionPool().setValidateOnCheckIn(true);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains("true"));
  }

  @Test
  void isValidateOnCheckOut() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConnection().getConnectionPool().setValidateOnCheckOut(true);
    assertTrue(expected.getConnection().getConnectionPool().isValidateOnCheckOut());

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConnection().getConnectionPool().setValidateOnCheckOut(true);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains("true"));
  }

  @Test
  void isValidatePeriodically() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConnection().getConnectionPool().setValidatePeriodically(true);
    assertTrue(expected.getConnection().getConnectionPool().isValidatePeriodically());

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConnection().getConnectionPool().setValidatePeriodically(true);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains("true"));
  }

  @Test
  void getValidatePeriod() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConnection().getConnectionPool().getValidator().setValidatePeriod(Duration.ofMillis(123456789L));

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConnection().getConnectionPool().getValidator().setValidatePeriod(Duration.ofMillis(123456789L));

    assertEquals(expected, actual);
  }

  @Test
  void getPrunePeriod() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConnection().getConnectionPool().setPrunePeriod(Duration.ofMillis(123456789L));

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConnection().getConnectionPool().setPrunePeriod(Duration.ofMillis(123456789L));

    assertEquals(expected, actual);
  }

  @Test
  void getIdleTime() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConnection().getConnectionPool().setIdleTime(Duration.ofMillis(123456789L));

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConnection().getConnectionPool().setIdleTime(Duration.ofMillis(123456789L));

    assertEquals(expected, actual);
  }

  @Test
  void getBlockWaitTime() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConnection().getConnectionPool().setBlockWaitTime(Duration.ofMillis(123456789L));

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConnection().getConnectionPool().setBlockWaitTime(Duration.ofMillis(123456789L));

    assertEquals(expected, actual);
  }

  @Test
  void getSearchValidator() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConnection().getConnectionPool().setValidator(new ConnectionValidatorProperties());
    assertNotNull(expected.getConnection().getConnectionPool().getValidator());
  }
}