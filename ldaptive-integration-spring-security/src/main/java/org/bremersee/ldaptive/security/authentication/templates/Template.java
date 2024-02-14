package org.bremersee.ldaptive.security.authentication.templates;

import java.util.function.Function;
import org.bremersee.ldaptive.security.authentication.LdaptiveAuthenticationProperties;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

public enum Template {

  ACTIVE_DIRECTORY(TemplateMapper.INSTANCE::toActiveDirectory),

  OPEN_LDAP(TemplateMapper.INSTANCE::toOpenLdap),

  USER_CONTAINS_GROUPS(TemplateMapper.INSTANCE::toUserGroup),

  GROUP_CONTAINS_USERS(TemplateMapper.INSTANCE::toGroupUser);

  private final Function<LdaptiveAuthenticationProperties, LdaptiveAuthenticationProperties> mapFn;

  Template(Function<LdaptiveAuthenticationProperties, LdaptiveAuthenticationProperties> mapFn) {
    this.mapFn = mapFn;
  }

  public LdaptiveAuthenticationProperties applyTemplate(LdaptiveAuthenticationProperties source) {
    return mapFn.apply(source);
  }

  @Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
  public interface TemplateMapper {

    TemplateMapper INSTANCE = Mappers.getMapper(TemplateMapper.class);

    ActiveDirectoryTemplate toActiveDirectory(LdaptiveAuthenticationProperties props);

    OpenLdapTemplate toOpenLdap(LdaptiveAuthenticationProperties props);

    UserContainsGroupsTemplate toUserGroup(LdaptiveAuthenticationProperties props);

    GroupContainsUsersTemplate toGroupUser(LdaptiveAuthenticationProperties props);
  }

}
