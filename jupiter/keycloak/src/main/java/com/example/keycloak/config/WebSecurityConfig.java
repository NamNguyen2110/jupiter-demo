package com.example.keycloak.config;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.OAuth2Constants;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticatedActionsFilter;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
import org.keycloak.adapters.springsecurity.filter.KeycloakPreAuthActionsFilter;
import org.keycloak.adapters.springsecurity.filter.KeycloakSecurityContextRequestFilter;
import org.keycloak.adapters.springsecurity.management.HttpSessionManager;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.Principal;
import java.util.List;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan(basePackageClasses = KeycloakSecurityComponents.class)
public class WebSecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

  @Value("${keycloak-client.client-id}")
  private String kcClientId;
  @Value("${keycloak-client.client-secret}")
  private String kcClientSecret;

  // not remember session
  @Override
  protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
    return new NullAuthenticatedSessionStrategy();
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) {
    KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
    keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
    auth.authenticationProvider(keycloakAuthenticationProvider);
  }

  @Bean
  public FilterRegistrationBean<?> keycloakAuthenticationProcessingFilterRegistrationBean(KeycloakAuthenticationProcessingFilter filter) {
    FilterRegistrationBean<?> registrationBean = new FilterRegistrationBean<>(filter);
    registrationBean.setEnabled(false);
    return registrationBean;
  }

  @Bean
  public FilterRegistrationBean<?> keycloakPreAuthActionsFilterRegistrationBean(KeycloakPreAuthActionsFilter filter) {
    FilterRegistrationBean<?> registrationBean = new FilterRegistrationBean<>(filter);
    registrationBean.setEnabled(false);
    return registrationBean;
  }

  @Bean
  public FilterRegistrationBean<?> keycloakAuthenticatedActionsFilterBean(KeycloakAuthenticatedActionsFilter filter) {
    FilterRegistrationBean<?> registrationBean = new FilterRegistrationBean<>(filter);
    registrationBean.setEnabled(false);
    return registrationBean;
  }

  @Bean
  public FilterRegistrationBean<?> keycloakSecurityContextRequestFilterBean(KeycloakSecurityContextRequestFilter filter) {
    FilterRegistrationBean<?> registrationBean = new FilterRegistrationBean<>(filter);
    registrationBean.setEnabled(false);
    return registrationBean;
  }

  @Bean
  @Override
  @ConditionalOnMissingBean(HttpSessionManager.class)
  protected HttpSessionManager httpSessionManager() {
    return new HttpSessionManager();
  }

  /**
   * The Keycloak Admin client that provides the service-account Access-Token
   *
   * @param props
   * @return
   */
  @Bean
  public Keycloak keycloakInstance(KeycloakSpringBootProperties props) {
    return KeycloakBuilder.builder() //
        .serverUrl(props.getAuthServerUrl()) //
        .realm(props.getRealm()) //
        .grantType(OAuth2Constants.CLIENT_CREDENTIALS) //
        .clientId(kcClientId) //
        .clientSecret(kcClientSecret) //
        .build();
  }

  /**
   * Allows to inject requests scoped wrapper for {@link KeycloakSecurityContext}.
   * <p>
   * Returns the {@link KeycloakSecurityContext} from the Spring {@link ServletRequestAttributes}'s {@link Principal}.
   * <p>
   * The principal must support retrieval of the KeycloakSecurityContext, so at this point, only {@link KeycloakPrincipal} values and
   * {@link KeycloakAuthenticationToken} are supported.
   *
   * @return the current <code>KeycloakSecurityContext</code>
   */
  @Bean
  @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
  public KeycloakSecurityContext provideKeycloakSecurityContext() {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    Principal principal = attributes.getRequest().getUserPrincipal();
    if (principal == null) {
      return null;
    }
    if (principal instanceof KeycloakAuthenticationToken) {
      principal = Principal.class.cast(KeycloakAuthenticationToken.class.cast(principal).getPrincipal());
    }
    if (principal instanceof KeycloakPrincipal) {
      return KeycloakPrincipal.class.cast(principal).getKeycloakSecurityContext();
    }
    return null;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    super.configure(http);
    http
        .cors().configurationSource(corsConfigurationSource()).and().csrf().disable()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        .antMatchers("/actuator/info", "/actuator/health/**",
            "/v3/api-docs", "/v2/api-docs", "/configuration/**",
            "/v3/api-docs/**", "/v2/api-docs/**",
            "/swagger-resources/**", "/swagger-ui/**",
            "/swagger-ui.html",
            "/webjars/**", "/api-docs/**").permitAll()
        .antMatchers("/").permitAll()
        .anyRequest().authenticated();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    List<String> allowOrigins = List.of("*");
    configuration.setAllowedOrigins(allowOrigins);
    configuration.setAllowedMethods(List.of("*"));
    configuration.setAllowedHeaders(List.of("*"));
    //in case authentication is enabled this flag MUST be set, otherwise CORS requests will fail
//    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

}