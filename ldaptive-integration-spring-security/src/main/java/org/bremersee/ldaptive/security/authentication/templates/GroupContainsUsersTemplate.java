package org.bremersee.ldaptive.security.authentication.templates;

import org.bremersee.ldaptive.security.authentication.LdaptiveAuthenticationProperties;

public class GroupContainsUsersTemplate
    extends LdaptiveAuthenticationProperties {

  @Override
  public GroupFetchStrategy getGroupFetchStrategy() {
    return GroupFetchStrategy.GROUP_CONTAINS_USERS;
  }

}
