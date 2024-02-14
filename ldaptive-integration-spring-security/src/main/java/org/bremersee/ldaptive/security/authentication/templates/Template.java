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

import java.util.function.Function;
import org.bremersee.ldaptive.security.authentication.LdaptiveAuthenticationProperties;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

/**
 * The enum Template.
 *
 * @author Christian Bremer
 */
public enum Template {

  /**
   * Active directory template.
   */
  ACTIVE_DIRECTORY(TemplateMapper.INSTANCE::toActiveDirectory),

  /**
   * Open ldap template.
   */
  OPEN_LDAP(TemplateMapper.INSTANCE::toOpenLdap),

  /**
   * User contains groups template.
   */
  USER_CONTAINS_GROUPS(TemplateMapper.INSTANCE::toUserGroup),

  /**
   * Group contains users template.
   */
  GROUP_CONTAINS_USERS(TemplateMapper.INSTANCE::toGroupUser);

  private final Function<LdaptiveAuthenticationProperties, LdaptiveAuthenticationProperties> mapFn;

  Template(Function<LdaptiveAuthenticationProperties, LdaptiveAuthenticationProperties> mapFn) {
    this.mapFn = mapFn;
  }

  /**
   * Apply template ldaptive authentication properties.
   *
   * @param source the source
   * @return the ldaptive authentication properties
   */
  public LdaptiveAuthenticationProperties applyTemplate(LdaptiveAuthenticationProperties source) {
    return mapFn.apply(source);
  }

  /**
   * The interface Template mapper.
   */
  @Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
  public interface TemplateMapper {

    /**
     * The constant INSTANCE.
     */
    TemplateMapper INSTANCE = Mappers.getMapper(TemplateMapper.class);

    /**
     * To active directory active directory template.
     *
     * @param props the props
     * @return the active directory template
     */
    ActiveDirectoryTemplate toActiveDirectory(LdaptiveAuthenticationProperties props);

    /**
     * To open ldap open ldap template.
     *
     * @param props the props
     * @return the open ldap template
     */
    OpenLdapTemplate toOpenLdap(LdaptiveAuthenticationProperties props);

    /**
     * To user group user contains groups template.
     *
     * @param props the props
     * @return the user contains groups template
     */
    UserContainsGroupsTemplate toUserGroup(LdaptiveAuthenticationProperties props);

    /**
     * To group user group contains users template.
     *
     * @param props the props
     * @return the group contains users template
     */
    GroupContainsUsersTemplate toGroupUser(LdaptiveAuthenticationProperties props);
  }

}
