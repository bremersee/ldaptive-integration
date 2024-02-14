package org.bremersee.ldaptive.security.authentication;

import lombok.Getter;
import org.bremersee.ldaptive.security.authentication.templates.ActiveDirectoryAccountControlEvaluator;
import org.bremersee.ldaptive.security.authentication.templates.NoAccountControlEvaluator;

@Getter
public enum AccountControlEvaluatorReference {

  NONE(new NoAccountControlEvaluator()),

  ACTIVE_DIRECTORY(new ActiveDirectoryAccountControlEvaluator());

  private final AccountControlEvaluator evaluator;

  AccountControlEvaluatorReference(AccountControlEvaluator evaluator) {
    this.evaluator = evaluator;
  }

}
