package org.bremersee.ldaptive.serializable;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapUtils;

/**
 * The type Ser ldap attr test.
 */
class SerLdapAttrTest {

  private static final SerLdapAttr emptyLdapAttr = new SerLdapAttr(
      new LdapAttribute("uid"));

  private SerLdapAttr createSerLdapAttr(boolean binary) {
    LdapAttribute attr = new LdapAttribute("uid", "test");
    attr.setBinary(binary);
    return new SerLdapAttr(attr);
  }

  /**
   * Size.
   *
   * @param binary the binary
   */
  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void size(boolean binary) {
    SerLdapAttr ldapAttr = createSerLdapAttr(binary);
    assertThat(ldapAttr.size())
        .isEqualTo(1);
  }

  /**
   * Gets string value.
   *
   * @param binary the binary
   */
  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void getStringValue(boolean binary) {
    SerLdapAttr ldapAttr = createSerLdapAttr(binary);
    if (binary) {
      assertThat(ldapAttr.getStringValue())
          .isEqualTo(LdapUtils.base64Encode("test".getBytes(StandardCharsets.UTF_8)));
    } else {
      assertThat(ldapAttr.getStringValue())
          .isEqualTo("test");
    }
  }

  /**
   * Gets string value empty.
   */
  @Test
  void getStringValueEmpty() {
    assertThat(emptyLdapAttr.getStringValue())
        .isNull();
  }

  /**
   * Gets string values.
   *
   * @param binary the binary
   */
  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void getStringValues(boolean binary) {
    SerLdapAttr ldapAttr = createSerLdapAttr(binary);
    if (binary) {
      assertThat(ldapAttr.getStringValues())
          .containsExactly(LdapUtils.base64Encode("test".getBytes(StandardCharsets.UTF_8)));
    } else {
      assertThat(ldapAttr.getStringValues())
          .containsExactly("test");
    }
  }

  /**
   * Has value.
   *
   * @param binary the binary
   */
  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void hasValue(boolean binary) {
    SerLdapAttr ldapAttr = createSerLdapAttr(binary);
    if (binary) {
      assertThat(ldapAttr.hasValue("test".getBytes(StandardCharsets.UTF_8)))
          .isTrue();
    } else {
      assertThat(ldapAttr.hasValue("test"))
          .isTrue();
    }
  }

  /**
   * To map.
   *
   * @param binary the binary
   */
  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void toMap(boolean binary) {
    SerLdapAttr ldapAttr = createSerLdapAttr(binary);

    LdapAttribute attr = new LdapAttribute("uid", "test");
    attr.setBinary(binary);
    LdapEntry ldapEntry = new LdapEntry();
    ldapEntry.addAttributes(attr);

    assertThat(SerLdapAttr.toMap(ldapEntry))
        .isEqualTo(Map.of("uid", ldapAttr));
  }

  /**
   * To map from null.
   */
  @Test
  void toMapFromNull() {
    assertThat(SerLdapAttr.toMap(null))
        .isEmpty();
  }

  /**
   * Test equals.
   *
   * @param binary the binary
   */
  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void testEquals(boolean binary) {
    SerLdapAttr ldapAttr = createSerLdapAttr(binary);
    assertThat(ldapAttr.equals(createSerLdapAttr(binary)))
        .isTrue();
  }

  /**
   * Test hash code.
   *
   * @param binary the binary
   */
  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void testHashCode(boolean binary) {
    SerLdapAttr ldapAttr = createSerLdapAttr(binary);
    assertThat(ldapAttr.hashCode())
        .isEqualTo(createSerLdapAttr(binary).hashCode());
  }

  /**
   * Gets attribute name.
   *
   * @param binary the binary
   */
  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void getAttributeName(boolean binary) {
    SerLdapAttr ldapAttr = createSerLdapAttr(binary);
    assertThat(ldapAttr.getAttributeName())
        .isEqualTo("uid");
  }

  /**
   * Gets binary value.
   *
   * @param binary the binary
   */
  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void getBinaryValue(boolean binary) {
    SerLdapAttr ldapAttr = createSerLdapAttr(binary);
    assertThat(ldapAttr.getBinaryValue())
        .isEqualTo("test".getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Gets binary value empty.
   */
  @Test
  void getBinaryValueEmpty() {
    assertThat(emptyLdapAttr.getBinaryValue())
        .isNull();
  }

  /**
   * Gets binary values.
   *
   * @param binary the binary
   */
  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void getBinaryValues(boolean binary) {
    SerLdapAttr ldapAttr = createSerLdapAttr(binary);
    assertThat(ldapAttr.getBinaryValues())
        .containsExactly("test".getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Is binary.
   *
   * @param binary the binary
   */
  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void isBinary(boolean binary) {
    SerLdapAttr ldapAttr = createSerLdapAttr(binary);
    assertThat(ldapAttr.isBinary())
        .isEqualTo(binary);
  }

  /**
   * Test to string.
   *
   * @param binary the binary
   */
  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void testToString(boolean binary) {
    SerLdapAttr ldapAttr = createSerLdapAttr(binary);
    assertThat(ldapAttr.toString())
        .contains("uid");
  }

  /**
   * Test serialization.
   *
   * @throws Exception the exception
   */
  @Test
  void testSerialization() throws Exception {
    SerLdapAttr expected = createSerLdapAttr(false);
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    try (ObjectOutputStream out = new ObjectOutputStream(bout)) {
      out.writeObject(expected);
    }
    ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
    try (ObjectInputStream in = new ObjectInputStream(bin)) {
      SerLdapAttr actual = (SerLdapAttr) in.readObject();
      assertThat(actual)
          .isEqualTo(expected);
    }
  }
}