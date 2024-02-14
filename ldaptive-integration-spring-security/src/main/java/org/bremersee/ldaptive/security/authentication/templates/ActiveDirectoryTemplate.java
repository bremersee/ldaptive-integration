package org.bremersee.ldaptive.security.authentication.templates;

import org.bremersee.ldaptive.security.authentication.AccountControlEvaluatorReference;
import org.ldaptive.SearchScope;

public class ActiveDirectoryTemplate
    extends UserContainsGroupsTemplate {

  public ActiveDirectoryTemplate() {
    setUserObjectClass("user");
    setUserUidAttribute("sAMAccountName");
    setPasswordAttribute(""); // userPassword
    setRealNameAttribute("cn");
    setEmailAttribute("mail");
    setUserFindOneSearchScope(SearchScope.ONELEVEL);
    setMemberAttribute("memberOf");
    setAccountControlEvaluator(AccountControlEvaluatorReference.ACTIVE_DIRECTORY);
  }

}
