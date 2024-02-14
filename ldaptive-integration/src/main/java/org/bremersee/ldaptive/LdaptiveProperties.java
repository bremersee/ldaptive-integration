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

package org.bremersee.ldaptive;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.ldaptive.ReturnAttributes;
import org.ldaptive.SearchConnectionValidator;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchScope;

/**
 * The ldap properties.
 *
 * @author Christian Bremer
 */
@Getter
@Setter
@ToString(exclude = {"bindCredentials"})
@EqualsAndHashCode(exclude = {"bindCredentials"})
@NoArgsConstructor
public class LdaptiveProperties {

  /**
   * URL to the LDAP(s).
   */
  private String ldapUrl = "ldap://localhost:12389";

  /**
   * Duration of time that connects will block.
   */
  private Duration connectTimeout = Duration.ofMinutes(1);

  /**
   * Duration of time to wait for responses.
   */
  private Duration responseTimeout = Duration.ofMinutes(1);

  /**
   * Duration of time that operations will block on reconnects, should generally be longer than
   * connect timeout.
   */
  private Duration reconnectTimeout = Duration.ofMinutes(2);

  /**
   * Whether to automatically reconnect to the server when a connection is lost. Default is true.
   */
  private boolean autoReconnect = true;

  private int reconnectAttempts = 5;

  private Duration reconnectBackoffDelay = Duration.ofSeconds(2);

  private double reconnectBackoffMultiplier = 1.;

  /**
   * Whether pending operations should be replayed after a reconnect. Default is true.
   */
  private boolean autoReplay = true;

  /**
   * Connect to LDAP using startTLS.
   */
  private boolean useStartTls;

  /**
   * Name of the trust certificates to use for the SSL connection.
   */
  private String trustCertificates;

  /**
   * Name of the authentication certificate to use for the SSL connection.
   */
  private String authenticationCertificate;

  /**
   * Name of the key to use for the SSL connection.
   */
  private String authenticationKey;

  /**
   * DN to bind as before performing operations.
   */
  private String bindDn;

  /**
   * Credential for the bind DN.
   */
  private String bindCredentials;

  /**
   * Perform a fast bind, if no credentials are present.
   */
  private boolean fastBind = false;

  /**
   * Specifies whether the connection should be pooled or not. Default is {@code false}.
   */
  private boolean pooled = false;

  /**
   * Duration to wait for an available connection.
   */
  private Duration blockWaitTime = Duration.ofMinutes(1);

  /**
   * Minimum pool size.
   */
  private int minPoolSize = 3;

  /**
   * Maximum pool size.
   */
  private int maxPoolSize = 10;

  /**
   * Whether to connect to the ldap on connection creation.
   */
  private boolean connectOnCreate = true;

  /**
   * Whether initialize should throw if pooling configuration requirements are not met.
   */
  private boolean failFastInitialize = true;

  /**
   * Whether the ldap object should be validated when returned to the pool.
   */
  private boolean validateOnCheckIn = false;

  /**
   * Whether the ldap object should be validated when given from the pool.
   */
  private boolean validateOnCheckOut = false;

  /**
   * Whether the pool should be validated periodically.
   */
  private boolean validatePeriodically = false;

  /**
   * Validation period.
   */
  private Duration validatePeriod = Duration.ofMinutes(30);

  /**
   * Maximum length of time a connection validation should block.
   */
  private Duration validateTimeout = Duration.ofSeconds(5);

  private SearchValidatorProperties searchValidator = new SearchValidatorProperties();

  /**
   * Prune period.
   */
  private Duration prunePeriod = Duration.ofMinutes(5);

  /**
   * Idle time.
   */
  private Duration idleTime = Duration.ofMinutes(10);

  /**
   * Create search connection validator search connection validator.
   *
   * @return the search connection validator
   */
  public SearchConnectionValidator createSearchConnectionValidator() {
    return new SearchConnectionValidator(
        validatePeriod,
        validateTimeout,
        searchValidator.getSearchRequest().createSearchRequest());
  }

  /**
   * The search validator properties.
   */
  @Data
  @NoArgsConstructor
  public static class SearchValidatorProperties {

    private SearchRequestProperties searchRequest = new SearchRequestProperties();

    /**
     * The search request properties.
     */
    @Data
    @NoArgsConstructor
    public static class SearchRequestProperties {

      private String baseDn;

      private SearchFilterProperties searchFilter = new SearchFilterProperties();

      private Integer sizeLimit;

      private SearchScope searchScope; // = SearchScope.ONELEVEL;

      private List<String> returnAttributes = new ArrayList<>();

      /**
       * Gets the return attributes as array.
       *
       * @return the return attributes as array
       */
      public String[] returnAttributesAsArray() {
        if (returnAttributes.isEmpty()) {
          return ReturnAttributes.NONE.value();
        }
        return returnAttributes.toArray(new String[0]);
      }

      /**
       * Create search request.
       *
       * @return the search request
       */
      public SearchRequest createSearchRequest() {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setBaseDn(Objects.requireNonNullElse(getBaseDn(), ""));
        if (Objects.nonNull(getSearchFilter().getFilter())
            && !getSearchFilter().getFilter().isEmpty()) {
          searchRequest.setFilter(getSearchFilter().getFilter());
        }
        searchRequest.setReturnAttributes(returnAttributesAsArray());
        if (getSearchScope() != null) {
          searchRequest.setSearchScope(getSearchScope());
        }
        if (getSizeLimit() != null) {
          searchRequest.setSizeLimit(getSizeLimit());
        }
        return searchRequest;
      }

      /**
       * The search filter properties.
       */
      @Data
      @NoArgsConstructor
      public static class SearchFilterProperties {

        private String filter;

      }
    }
  }

}
