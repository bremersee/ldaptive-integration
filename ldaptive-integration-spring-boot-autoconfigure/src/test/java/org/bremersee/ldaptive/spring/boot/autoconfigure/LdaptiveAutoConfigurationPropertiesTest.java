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
    expected.getConfig().setLdapUrl(value);

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConfig().setLdapUrl(value);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains(value));
  }

  @Test
  void getConnectTimeout() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConfig().setConnectTimeout(Duration.ofMillis(123456789L));

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConfig().setConnectTimeout(Duration.ofMillis(123456789L));

    assertEquals(expected, actual);
  }

  @Test
  void getResponseTimeout() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConfig().setResponseTimeout(Duration.ofMillis(123456789L));

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConfig().setResponseTimeout(Duration.ofMillis(123456789L));

    assertEquals(expected, actual);
  }

  @Test
  void isUseStartTls() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConfig().setUseStartTls(true);
    assertTrue(expected.getConfig().isUseStartTls());

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConfig().setUseStartTls(true);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains("true"));
  }

  @Test
  void getTrustCertificates() {
    String value = UUID.randomUUID().toString();
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConfig().getSslConfig().setTrustCertificates(value);

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConfig().getSslConfig().setTrustCertificates(value);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains(value));
  }

  @Test
  void getAuthenticationCertificate() {
    String value = UUID.randomUUID().toString();
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConfig().getSslConfig().setAuthenticationCertificate(value);

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConfig().getSslConfig().setAuthenticationCertificate(value);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains(value));
  }

  @Test
  void getAuthenticationKey() {
    String value = UUID.randomUUID().toString();
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConfig().getSslConfig().setAuthenticationKey(value);

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConfig().getSslConfig().setAuthenticationKey(value);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains(value));
  }

  @Test
  void getBindDn() {
    String value = UUID.randomUUID().toString();
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConfig().setBindDn(value);

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConfig().setBindDn(value);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains(value));
  }

  @Test
  void getBindCredential() {
    String value = UUID.randomUUID().toString();
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConfig().setBindCredentials(value);
    assertEquals(value, expected.getConfig().getBindCredentials());
  }

  @Test
  void isPooled() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConfig().setPooled(true);
    assertTrue(expected.getConfig().isPooled());

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConfig().setPooled(true);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains("true"));
  }

  @Test
  void getMinPoolSize() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConfig().getConnectionPool().setMinPoolSize(1234567);

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConfig().getConnectionPool().setMinPoolSize(1234567);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains("1234567"));
  }

  @Test
  void getMaxPoolSize() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConfig().getConnectionPool().setMaxPoolSize(1234567);

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConfig().getConnectionPool().setMaxPoolSize(1234567);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains("1234567"));
  }

  @Test
  void isValidateOnCheckIn() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConfig().getConnectionPool().setValidateOnCheckIn(true);
    assertTrue(expected.getConfig().getConnectionPool().isValidateOnCheckIn());

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConfig().getConnectionPool().setValidateOnCheckIn(true);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains("true"));
  }

  @Test
  void isValidateOnCheckOut() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConfig().getConnectionPool().setValidateOnCheckOut(true);
    assertTrue(expected.getConfig().getConnectionPool().isValidateOnCheckOut());

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConfig().getConnectionPool().setValidateOnCheckOut(true);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains("true"));
  }

  @Test
  void isValidatePeriodically() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConfig().getConnectionPool().setValidatePeriodically(true);
    assertTrue(expected.getConfig().getConnectionPool().isValidatePeriodically());

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConfig().getConnectionPool().setValidatePeriodically(true);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains("true"));
  }

  @Test
  void getValidatePeriod() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConfig().getConnectionPool().getValidator().setValidatePeriod(Duration.ofMillis(123456789L));

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConfig().getConnectionPool().getValidator().setValidatePeriod(Duration.ofMillis(123456789L));

    assertEquals(expected, actual);
  }

  @Test
  void getPrunePeriod() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConfig().getConnectionPool().setPrunePeriod(Duration.ofMillis(123456789L));

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConfig().getConnectionPool().setPrunePeriod(Duration.ofMillis(123456789L));

    assertEquals(expected, actual);
  }

  @Test
  void getIdleTime() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConfig().getConnectionPool().setIdleTime(Duration.ofMillis(123456789L));

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConfig().getConnectionPool().setIdleTime(Duration.ofMillis(123456789L));

    assertEquals(expected, actual);
  }

  @Test
  void getBlockWaitTime() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConfig().getConnectionPool().setBlockWaitTime(Duration.ofMillis(123456789L));

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConfig().getConnectionPool().setBlockWaitTime(Duration.ofMillis(123456789L));

    assertEquals(expected, actual);
  }

  @Test
  void getSearchValidator() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConfig().getConnectionPool().setValidator(new ConnectionValidatorProperties());
    assertNotNull(expected.getConfig().getConnectionPool().getValidator());
  }
}