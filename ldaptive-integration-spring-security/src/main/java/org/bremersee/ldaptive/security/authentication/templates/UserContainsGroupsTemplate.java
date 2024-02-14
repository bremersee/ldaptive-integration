package org.bremersee.ldaptive.security.authentication.templates;

import org.bremersee.ldaptive.security.authentication.LdaptiveAuthenticationProperties;

public class UserContainsGroupsTemplate
    extends LdaptiveAuthenticationProperties {

  @Override
  public GroupFetchStrategy getGroupFetchStrategy() {
    return GroupFetchStrategy.USER_CONTAINS_GROUPS;
  }

}
