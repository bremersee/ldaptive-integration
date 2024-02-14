package org.bremersee.ldaptive;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LdaptivePropertiesTest {

  @Test
  void createSearchConnectionValidator() {

    LdaptiveProperties properties = new LdaptiveProperties();
    assertThat(properties.createSearchConnectionValidator())
        .isNotNull();
  }
}