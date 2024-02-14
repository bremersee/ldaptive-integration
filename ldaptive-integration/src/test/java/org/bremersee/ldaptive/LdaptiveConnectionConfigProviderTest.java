package org.bremersee.ldaptive;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.ldaptive.LdaptiveConnectionConfigProvider.DefaultLdaptiveConnectionConfigProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({SoftAssertionsExtension.class})
class LdaptiveConnectionConfigProviderTest {

  @Test
  void getConnectionConfig(SoftAssertions softly) {
    LdaptiveProperties properties = new LdaptiveProperties();
    LdaptiveConnectionConfigProvider target = new DefaultLdaptiveConnectionConfigProvider(
        properties);

    softly
        .assertThat(target.getConnectionConfig().getConnectionInitializers())
        .isEmpty();

    properties.setFastBind(true);
    softly
        .assertThat(target.getConnectionConfig().getConnectionInitializers())
        .hasSize(1);

    properties.setFastBind(false);
    properties.setBindDn("cn=admin");
    properties.setBindCredentials("secret");
    softly
        .assertThat(target.getConnectionConfig().getConnectionInitializers())
        .hasSize(1);
  }

  @Test
  void testGetConnectionConfig(SoftAssertions softly) {
    LdaptiveProperties properties = new LdaptiveProperties();
    LdaptiveConnectionConfigProvider target = new DefaultLdaptiveConnectionConfigProvider(
        properties);

    softly
        .assertThat(target.getConnectionConfig(null, null)
            .getConnectionInitializers())
        .isEmpty();

    softly
        .assertThat(target.getConnectionConfig("cn=admin", "secret")
            .getConnectionInitializers())
        .hasSize(1);
  }
}