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

  public String getUserFindOneFilter() {
    if (ObjectUtils.isEmpty(userFindOneFilter)
        && !ObjectUtils.isEmpty(getUserObjectClass())
        && !ObjectUtils.isEmpty(getUserUidAttribute())) {
      return String
          .format("(&(objectClass=%s)(%s={0}))", getUserObjectClass(), getUserUidAttribute());
    }
    return userFindOneFilter;
  }

  public final SearchScope getUserFindOneSearchScope() {
    return Optional.ofNullable(userFindOneSearchScope).orElse(SearchScope.ONELEVEL);
  }

  public final SearchScope getGroupSearchScope() {
    return Optional.ofNullable(groupSearchScope).orElse(SearchScope.ONELEVEL);
  }

  public final CaseTransformation getRoleCaseTransformation() {
    return Optional.ofNullable(roleCaseTransformation).orElse(CaseTransformation.NONE);
  }

  public AccountControlEvaluatorReference getAccountControlEvaluator() {
    return Optional.ofNullable(accountControlEvaluator)
        .orElse(AccountControlEvaluatorReference.NONE);
  }

  public enum GroupFetchStrategy {

    USER_CONTAINS_GROUPS,

    GROUP_CONTAINS_USERS
  }

  public enum CaseTransformation {

    NONE,

    TO_UPPER_CASE,

    TO_LOWER_CASE
  }

}
