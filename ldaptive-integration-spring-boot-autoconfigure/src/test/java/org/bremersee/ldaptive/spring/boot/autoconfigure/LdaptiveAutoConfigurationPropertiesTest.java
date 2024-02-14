package org.bremersee.ldaptive.spring.boot.autoconfigure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.UUID;
import org.bremersee.ldaptive.LdaptiveProperties.SearchValidatorProperties;
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
    expected.getConfig().setTrustCertificates(value);

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConfig().setTrustCertificates(value);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains(value));
  }

  @Test
  void getAuthenticationCertificate() {
    String value = UUID.randomUUID().toString();
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConfig().setAuthenticationCertificate(value);

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConfig().setAuthenticationCertificate(value);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains(value));
  }

  @Test
  void getAuthenticationKey() {
    String value = UUID.randomUUID().toString();
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConfig().setAuthenticationKey(value);

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConfig().setAuthenticationKey(value);

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
    expected.getConfig().setMinPoolSize(1234567);

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConfig().setMinPoolSize(1234567);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains("1234567"));
  }

  @Test
  void getMaxPoolSize() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConfig().setMaxPoolSize(1234567);

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConfig().setMaxPoolSize(1234567);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains("1234567"));
  }

  @Test
  void isValidateOnCheckIn() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConfig().setValidateOnCheckIn(true);
    assertTrue(expected.getConfig().isValidateOnCheckIn());

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConfig().setValidateOnCheckIn(true);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains("true"));
  }

  @Test
  void isValidateOnCheckOut() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConfig().setValidateOnCheckOut(true);
    assertTrue(expected.getConfig().isValidateOnCheckOut());

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConfig().setValidateOnCheckOut(true);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains("true"));
  }

  @Test
  void isValidatePeriodically() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConfig().setValidatePeriodically(true);
    assertTrue(expected.getConfig().isValidatePeriodically());

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConfig().setValidatePeriodically(true);

    assertEquals(expected, actual);
    assertTrue(expected.toString().contains("true"));
  }

  @Test
  void getValidatePeriod() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConfig().setValidatePeriod(Duration.ofMillis(123456789L));

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConfig().setValidatePeriod(Duration.ofMillis(123456789L));

    assertEquals(expected, actual);
  }

  @Test
  void getPrunePeriod() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConfig().setPrunePeriod(Duration.ofMillis(123456789L));

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConfig().setPrunePeriod(Duration.ofMillis(123456789L));

    assertEquals(expected, actual);
  }

  @Test
  void getIdleTime() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConfig().setIdleTime(Duration.ofMillis(123456789L));

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConfig().setIdleTime(Duration.ofMillis(123456789L));

    assertEquals(expected, actual);
  }

  @Test
  void getBlockWaitTime() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConfig().setBlockWaitTime(Duration.ofMillis(123456789L));

    LdaptiveAutoConfigurationProperties actual = new LdaptiveAutoConfigurationProperties();
    actual.getConfig().setBlockWaitTime(Duration.ofMillis(123456789L));

    assertEquals(expected, actual);
  }

  @Test
  void getSearchValidator() {
    LdaptiveAutoConfigurationProperties expected = new LdaptiveAutoConfigurationProperties();
    expected.getConfig().setSearchValidator(new SearchValidatorProperties());
    assertNotNull(expected.getConfig().getSearchValidator());
  }
}