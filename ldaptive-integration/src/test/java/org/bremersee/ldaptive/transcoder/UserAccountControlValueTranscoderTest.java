package org.bremersee.ldaptive.transcoder;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({SoftAssertionsExtension.class})
class UserAccountControlValueTranscoderTest {

  @Test
  void getUserAccountControlValue(SoftAssertions softly) {
    int enabled = UserAccountControlValueTranscoder.getUserAccountControlValue(true, null);
    int disabled = UserAccountControlValueTranscoder.getUserAccountControlValue(false, null);
    softly
        .assertThat(enabled + UserAccountControlValueTranscoder.ACCOUNT_DISABLED)
        .isEqualTo(disabled);

    int newEnabled = UserAccountControlValueTranscoder.getUserAccountControlValue(true, disabled);
    softly
        .assertThat(newEnabled)
        .isEqualTo(enabled);

    int newDisabled = UserAccountControlValueTranscoder.getUserAccountControlValue(false, enabled);
    softly
        .assertThat(newDisabled)
        .isEqualTo(disabled);
  }

  @Test
  void isUserAccountEnabled(SoftAssertions softly) {
    softly
        .assertThat(UserAccountControlValueTranscoder.isUserAccountEnabled(66048))
        .isTrue();
    softly
        .assertThat(UserAccountControlValueTranscoder.isUserAccountEnabled(66050))
        .isFalse();
    //noinspection ConstantValue
    softly
        .assertThat(UserAccountControlValueTranscoder.isUserAccountEnabled(null, false))
        .isFalse();
    softly
        .assertThat(UserAccountControlValueTranscoder
            .isUserAccountEnabled(UserAccountControlValueTranscoder.ACCOUNT_DISABLED, true))
        .isFalse();
  }

  @Test
  void decodeStringValue(SoftAssertions softly) {
    assertThat(new UserAccountControlValueTranscoder().decodeStringValue("2"))
        .isEqualTo(2);
    softly
        .assertThat(new UserAccountControlValueTranscoder().decodeStringValue(null))
        .isEqualTo(66048);
  }

  @Test
  void encodeStringValue(SoftAssertions softly) {
    softly
        .assertThat(new UserAccountControlValueTranscoder().encodeStringValue(2))
        .isEqualTo("2");
    softly
        .assertThat(new UserAccountControlValueTranscoder().encodeStringValue(null))
        .isEqualTo("66048");
  }

  @Test
  void getType() {
    assertThat(new UserAccountControlValueTranscoder().getType())
        .isEqualTo(Integer.class);
  }

  @Test
  void testToString() {
    assertThat(new UserAccountControlValueTranscoder().toString())
        .contains(UserAccountControlValueTranscoder.class.getSimpleName());
  }
}