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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bremersee.ldaptive.security.authentication.templates.Template;
import org.ldaptive.SearchScope;
import org.springframework.util.ObjectUtils;

/**
 * The type Ldaptive authentication properties.
 *
 * @author Christian Bremer
 */
@Data
@NoArgsConstructor
public class LdaptiveAuthenticationProperties {

  private String userBaseDn;

  private String userObjectClass;

  private String userUidAttribute;

  private String realNameAttribute;

  private String emailAttribute;

  private String passwordAttribute;

  private String userFindOneFilter;

  private SearchScope userFindOneSearchScope;

  private boolean ldapGroupsToRolesMappingEnabled = true;

  private GroupFetchStrategy groupFetchStrategy;

  private String memberAttribute;

  private String groupBaseDn;

  private SearchScope groupSearchScope;

  private String groupObjectClass;

  private String groupIdAttribute;

  private String groupMemberAttribute;

  private String groupMemberFormat;

  private Map<String, String> groupToRoleMapping = new LinkedHashMap<>();

  private List<String> defaultRoles = new ArrayList<>();

  private String rolePrefix;

  private String roleSpaceReplacement;

  private CaseTransformation roleCaseTransformation;

  private AccountControlEvaluatorReference accountControlEvaluator;

  /**
   * Gets user find one filter.
   *
   * @return the user find one filter
   */
  public String getUserFindOneFilter() {
    if (ObjectUtils.isEmpty(userFindOneFilter)
        && !ObjectUtils.isEmpty(getUserObjectClass())
        && !ObjectUtils.isEmpty(getUserUidAttribute())) {
      return String
          .format("(&(objectClass=%s)(%s={0}))", getUserObjectClass(), getUserUidAttribute());
    }
    return userFindOneFilter;
  }

  /**
   * Gets user find one search scope.
   *
   * @return the user find one search scope
   */
  public final SearchScope getUserFindOneSearchScope() {
    return Optional.ofNullable(userFindOneSearchScope).orElse(SearchScope.ONELEVEL);
  }

  /**
   * Gets group search scope.
   *
   * @return the group search scope
   */
  public final SearchScope getGroupSearchScope() {
    return Optional.ofNullable(groupSearchScope).orElse(SearchScope.ONELEVEL);
  }

  /**
   * Gets role case transformation.
   *
   * @return the role case transformation
   */
  public final CaseTransformation getRoleCaseTransformation() {
    return Optional.ofNullable(roleCaseTransformation).orElse(CaseTransformation.NONE);
  }

  /**
   * Gets account control evaluator.
   *
   * @return the account control evaluator
   */
  public AccountControlEvaluatorReference getAccountControlEvaluator() {
    return Optional.ofNullable(accountControlEvaluator)
        .orElse(AccountControlEvaluatorReference.NONE);
  }

  /**
   * The enum Group fetch strategy.
   */
  public enum GroupFetchStrategy {

    /**
     * User contains groups group fetch strategy.
     */
    USER_CONTAINS_GROUPS,

    /**
     * Group contains users group fetch strategy.
     */
    GROUP_CONTAINS_USERS
  }

  /**
   * The enum Case transformation.
   */
  public enum CaseTransformation {

    /**
     * None case transformation.
     */
    NONE,

    /**
     * To upper case case transformation.
     */
    TO_UPPER_CASE,

    /**
     * To lower case case transformation.
     */
    TO_LOWER_CASE
  }

}
