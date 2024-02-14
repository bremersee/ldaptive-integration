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

package org.bremersee.ldaptive.security.authentication;

import lombok.Getter;
import org.bremersee.ldaptive.security.authentication.templates.ActiveDirectoryAccountControlEvaluator;
import org.bremersee.ldaptive.security.authentication.templates.NoAccountControlEvaluator;

/**
 * The enum Account control evaluator reference.
 *
 * @author Christian Bremer
 */
@Getter
public enum AccountControlEvaluatorReference {

  /**
   * The None.
   */
  NONE(new NoAccountControlEvaluator()),

  /**
   * The Active directory.
   */
  ACTIVE_DIRECTORY(new ActiveDirectoryAccountControlEvaluator());

  private final AccountControlEvaluator evaluator;

  AccountControlEvaluatorReference(AccountControlEvaluator evaluator) {
    this.evaluator = evaluator;
  }

}
