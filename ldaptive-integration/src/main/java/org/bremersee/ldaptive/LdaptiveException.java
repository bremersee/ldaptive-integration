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

import java.io.Serial;
import lombok.EqualsAndHashCode;
import org.bremersee.exception.AbstractServiceExceptionBuilder;
import org.bremersee.exception.ErrorCodeAware;
import org.bremersee.exception.HttpStatusAware;
import org.bremersee.exception.ServiceException;
import org.bremersee.exception.ServiceExceptionBuilder;
import org.ldaptive.LdapException;
import org.ldaptive.ResultCode;

/**
 * The ldaptive exception.
 *
 * @author Christian Bremer
 */
@EqualsAndHashCode(callSuper = true)
public class LdaptiveException extends ServiceException implements HttpStatusAware, ErrorCodeAware {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new ldaptive exception.
   *
   * @param httpStatus the http status
   * @param errorCode the error code
   * @param reason the reason
   * @param cause the cause
   */
  protected LdaptiveException(
      int httpStatus,
      String errorCode,
      String reason,
      Throwable cause) {
    super(httpStatus, errorCode, reason, cause);
  }


  /**
   * Gets ldap exception (can be {@code null}).
   *
   * @return the ldap exception
   */
  public LdapException getLdapException() {
    return getCause() instanceof LdapException ? (LdapException) getCause() : null;
  }

  /**
   * Gets result code (can be {@code null}).
   *
   * @return the result code
   */
  public ResultCode getResultCode() {
    LdapException ldapException = getLdapException();
    return ldapException != null ? ldapException.getResultCode() : null;
  }

  /**
   * Creates a new service exception builder.
   *
   * @return the service exception builder
   */
  public static ServiceExceptionBuilder<? extends LdaptiveException> builder() {

    return new AbstractServiceExceptionBuilder<>() {

      @Serial
      private static final long serialVersionUID = 2L;

      @Override
      protected LdaptiveException buildWith(int httpStatus, String errorCode, String reason,
          Throwable cause) {
        return new LdaptiveException(httpStatus, errorCode, reason, cause);
      }
    };
  }

}
