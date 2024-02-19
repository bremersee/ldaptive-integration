/*
 * Copyright 2019 the original author or authors.
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

package org.bremersee.ldaptive.spring.boot.autoconfigure;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bremersee.ldaptive.LdaptiveProperties;
import org.ldaptive.SearchScope;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * The ldap properties.
 *
 * @author Christian Bremer
 */
@ConfigurationProperties(prefix = "bremersee.ldaptive")
@Data
@NoArgsConstructor
public class LdaptiveAutoConfigurationProperties {

  /**
   * Specifies whether ldap connection should be configured or not.
   */
  private boolean enabled = true;

  @NestedConfigurationProperty
  private LdaptiveProperties connection = new LdaptiveProperties();

  private LdaptiveAuthenticationAutoConfigurationProperties authentication
      = new LdaptiveAuthenticationAutoConfigurationProperties();

  @Data
  @NoArgsConstructor
  public static class LdaptiveAuthenticationAutoConfigurationProperties {

    private Template template = Template.ACTIVE_DIRECTORY;

    private UsernameToBindDnConverterProperty usernameToBindDnConverter;

    /**
     * The user base dn (like 'ou=people,dc=example,dc=org'). This value is always required.
     */
    private String userBaseDn;

    /**
     * The object class of the user (like 'inetOrgPerson'). The selected template contains a
     * default.
     */
    private String userObjectClass;

    /**
     * The username attribute of the user (like 'uid' or 'sAMAccountName'). The selected template
     * contains a default.
     */
    private String usernameAttribute;

    /**
     * Applies only for simple bind. The rdn attribute of the user. This is normally the same as the
     * username attribute.
     */
    private String userRdnAttribute;

    /**
     * The password attribute of the user (like 'userPassword'). If it is empty, a simple user bind
     * will be done with the credentials of the user for authentication. If it is present, the
     * connection to the ldap server must be done by a 'global' user and a password encoder that
     * fits your requirements must be present. The default password encoder only supports SHA, that
     * is insecure.
     */
    private String passwordAttribute;

    /**
     * The filter to find the user. If it is empty, it will be generated from 'userObjectClass' and
     * 'usernameAttribute' like this '(&(objectClass=inetOrgPerson)(uid={0}))'.
     */
    private String userFindOneFilter;

    /**
     * The scope to find a user. Default is 'one level'.
     */
    private SearchScope userFindOneSearchScope;

    /**
     * The real name attribute of the user. Default is 'cn'.
     */
    private String realNameAttribute;

    /**
     * The email attribute of the user. Default is 'mail';
     */
    private String emailAttribute;

    private AccountControlEvaluatorProperty accountControlEvaluator;


    private Boolean ldapGroupsToRolesMappingEnabled;

    private GroupFetchStrategy groupFetchStrategy;

    private String memberAttribute;

    private String groupBaseDn;

    private SearchScope groupSearchScope;

    private String groupObjectClass;

    private String groupIdAttribute;

    private String groupMemberAttribute;

    private String groupMemberFormat;

    private List<GroupToRoleMapping> groupToRoleMapping;

    private List<String> defaultRoles;

    private String rolePrefix;

    private CaseTransformation roleCaseTransformation;

    private List<StringReplacement> roleStringReplacements;

    public enum UsernameToBindDnConverterProperty {

      BY_USER_RDN_ATTRIBUTE,

      BY_DOMAIN_EMAIL;
    }

    public enum AccountControlEvaluatorProperty {

      /**
       * The None.
       */
      NONE,

      /**
       * The Active directory.
       */
      ACTIVE_DIRECTORY;
    }

    /**
     * The group fetch strategy.
     */
    public enum GroupFetchStrategy {

      /**
       * User contains groups fetch strategy.
       */
      USER_CONTAINS_GROUPS,

      /**
       * Group contains users fetch strategy.
       */
      GROUP_CONTAINS_USERS
    }

    @Data
    @NoArgsConstructor
    public static class GroupToRoleMapping {

      private String groupName;

      private String roleName;
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

    @Data
    @NoArgsConstructor
    public static class StringReplacement {

      /**
       * The regular expression to which the string is to be matched. '{@code [- ]}' for example
       * would replace every '-' and every space.
       */
      private String regex;

      /**
       * The string to be substituted for each match.
       */
      private String replacement;
    }

    /**
     * The templates for ldap authentication.
     *
     * @author Christian Bremer
     */
    public enum Template {

      /**
       * Active directory template.
       */
      ACTIVE_DIRECTORY,

      /**
       * Open ldap template.
       */
      OPEN_LDAP,

      /**
       * User contains groups template.
       */
      USER_CONTAINS_GROUPS,

      /**
       * Group contains users template.
       */
      GROUP_CONTAINS_USERS;
    }
  }
}
