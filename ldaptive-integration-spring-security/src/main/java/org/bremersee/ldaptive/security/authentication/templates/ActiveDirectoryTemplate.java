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

package org.bremersee.ldaptive.security.authentication.templates;

import org.bremersee.ldaptive.security.authentication.AccountControlEvaluatorReference;
import org.ldaptive.SearchScope;

/**
 * Template settings for Active Directory.
 *
 * @author Christian Bremer
 */
public class ActiveDirectoryTemplate
    extends UserContainsGroupsTemplate {

  /**
   * Instantiates a new Active directory template.
   */
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
