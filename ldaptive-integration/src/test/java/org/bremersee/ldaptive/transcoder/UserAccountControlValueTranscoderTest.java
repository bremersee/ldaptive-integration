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

package org.bremersee.ldaptive.transcoder;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The type User account control value transcoder test.
 *
 * @author Christian Bremer
 */
@ExtendWith({SoftAssertionsExtension.class})
class UserAccountControlValueTranscoderTest {

  /**
   * Gets user account control value.
   *
   * @param softly the softly
   */
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

  /**
   * Is user account enabled.
   *
   * @param softly the softly
   */
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

  /**
   * Decode string value.
   *
   * @param softly the softly
   */
  @Test
  void decodeStringValue(SoftAssertions softly) {
    assertThat(new UserAccountControlValueTranscoder().decodeStringValue("2"))
        .isEqualTo(2);
    softly
        .assertThat(new UserAccountControlValueTranscoder().decodeStringValue(null))
        .isEqualTo(66048);
  }

  /**
   * Encode string value.
   *
   * @param softly the softly
   */
  @Test
  void encodeStringValue(SoftAssertions softly) {
    softly
        .assertThat(new UserAccountControlValueTranscoder().encodeStringValue(2))
        .isEqualTo("2");
    softly
        .assertThat(new UserAccountControlValueTranscoder().encodeStringValue(null))
        .isEqualTo("66048");
  }

  /**
   * Gets type.
   */
  @Test
  void getType() {
    assertThat(new UserAccountControlValueTranscoder().getType())
        .isEqualTo(Integer.class);
  }

  /**
   * Test to string.
   */
  @Test
  void testToString() {
    assertThat(new UserAccountControlValueTranscoder().toString())
        .contains(UserAccountControlValueTranscoder.class.getSimpleName());
  }
}