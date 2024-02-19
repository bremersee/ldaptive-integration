/*
 * Copyright 2024 the original author or authors.
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

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.ldaptive.security.authentication.AccountControlEvaluator;
import org.bremersee.ldaptive.security.authentication.LdaptiveAuthenticationManager;
import org.bremersee.ldaptive.security.authentication.LdaptiveAuthenticationProperties;
import org.bremersee.ldaptive.security.authentication.ReactiveLdaptiveAuthenticationManager;
import org.bremersee.ldaptive.security.authentication.UsernameToBindDnConverter;
import org.bremersee.ldaptive.security.authentication.templates.Template;
import org.bremersee.ldaptive.spring.boot.autoconfigure.LdaptiveAutoConfigurationProperties.LdaptiveAuthenticationAutoConfigurationProperties;
import org.ldaptive.ConnectionConfig;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.util.ClassUtils;

@AutoConfiguration
@ConditionalOnClass(name = {
    "org.ldaptive.ConnectionFactory",
    "org.bremersee.ldaptive.LdaptiveTemplate",
    "org.bremersee.ldaptive.security.authentication.LdaptiveAuthenticationManager"
})
@ConditionalOnBean({ConnectionConfig.class})
@AutoConfigureAfter({LdaptiveAutoConfiguration.class})
@EnableConfigurationProperties(LdaptiveAutoConfigurationProperties.class)
@Slf4j
public class LdaptiveSecurityAutoConfiguration {

  private final LdaptiveAuthenticationProperties properties;

  public LdaptiveSecurityAutoConfiguration(LdaptiveAutoConfigurationProperties properties) {
    LdaptiveAuthenticationProperties authProps = AuthenticationPropertiesMapper.INSTANCE.map(
        properties.getAuthentication());
    Template template;
    try {
      template = Template.valueOf(properties.getAuthentication().getTemplate().name());
    } catch (Exception ignored) {
      template = null;
    }
    this.properties = Optional
        .ofNullable(template)
        .map(t -> t.applyTemplate(authProps))
        .orElse(authProps);
  }

  /**
   * Init.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void init() {
    log.info("""

            *********************************************************************************
            * {}
            * properties = {}
            *********************************************************************************""",
        ClassUtils.getUserClass(getClass()).getSimpleName(),
        properties);
  }

  @ConditionalOnMissingBean(LdaptivePasswordEncoderProvider.class)
  @Bean
  public LdaptivePasswordEncoderProvider ldaptivePasswordEncoderProvider() {
    return LdaptivePasswordEncoderProvider.defaultProvider();
  }

  @ConditionalOnWebApplication(type = Type.SERVLET)
  @Bean
  public LdaptiveAuthenticationManager ldaptiveAuthenticationManager(
      ConnectionConfig connectionConfig,
      ObjectProvider<UsernameToBindDnConverter> usernameToBindDnProvider,
      LdaptivePasswordEncoderProvider passwordEncoderProvider,
      ObjectProvider<AccountControlEvaluator> accountControlEvaluatorProvider) {

    LdaptiveAuthenticationManager manager = new LdaptiveAuthenticationManager(
        connectionConfig, properties);
    manager.setPasswordEncoder(passwordEncoderProvider.get());
    usernameToBindDnProvider.ifAvailable(manager::setUsernameToBindDnConverter);
    AccountControlEvaluator accountControlEvaluator = accountControlEvaluatorProvider
        .getIfAvailable(() -> properties.getAccountControlEvaluator().get());
    manager.setAccountControlEvaluator(accountControlEvaluator);
    return manager;
  }

  @ConditionalOnWebApplication(type = Type.REACTIVE)
  @Bean
  public ReactiveLdaptiveAuthenticationManager reactiveLdaptiveAuthenticationManager(
      ConnectionConfig connectionConfig,
      ObjectProvider<UsernameToBindDnConverter> usernameToBindDnProvider,
      LdaptivePasswordEncoderProvider passwordEncoderProvider,
      ObjectProvider<AccountControlEvaluator> accountControlEvaluatorProvider) {
    return new ReactiveLdaptiveAuthenticationManager(
        ldaptiveAuthenticationManager(
            connectionConfig,
            usernameToBindDnProvider,
            passwordEncoderProvider,
            accountControlEvaluatorProvider));
  }

  @Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
  interface AuthenticationPropertiesMapper {

    /**
     * The constant INSTANCE.
     */
    AuthenticationPropertiesMapper INSTANCE = Mappers
        .getMapper(AuthenticationPropertiesMapper.class);

    LdaptiveAuthenticationProperties map(
        LdaptiveAuthenticationAutoConfigurationProperties props);

  }

}
