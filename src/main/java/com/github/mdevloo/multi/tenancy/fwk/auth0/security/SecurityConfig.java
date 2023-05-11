package com.github.mdevloo.multi.tenancy.fwk.auth0.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static com.github.mdevloo.multi.tenancy.core.AppConstants.API;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

  private final String audience;
  private final String issuer;

  public SecurityConfig(
      @Value("${auth0.audience}") final String audience,
      @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") final String issuer) {
    this.audience = audience;
    this.issuer = issuer;
  }

  @Bean
  public SecurityFilterChain configure(final HttpSecurity http) throws Exception {
    http.cors();
    http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
    http.headers()
        .referrerPolicy(
            referrerPolicyConfig ->
                referrerPolicyConfig.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN));

    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    http.authorizeHttpRequests()
        .requestMatchers(API + "/**")
        .authenticated()
        .and()
        .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
    return http.build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedHeader("*");
    configuration.setAllowedOrigins(
        List.of()); /* Add Origin here for frontend: localhost:4200 for example */
    configuration.setAllowedMethods(Arrays.asList("OPTIONS", "HEAD", "GET", "POST", "DELETE"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  JwtDecoder jwtDecoder() {
    final NimbusJwtDecoder nimbusJwtDecoder = JwtDecoders.fromOidcIssuerLocation(this.issuer);
    final OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(this.audience);
    final OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(this.issuer);
    final OAuth2TokenValidator<Jwt> withAudience =
        new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

    nimbusJwtDecoder.setJwtValidator(withAudience);

    return nimbusJwtDecoder;
  }
}
