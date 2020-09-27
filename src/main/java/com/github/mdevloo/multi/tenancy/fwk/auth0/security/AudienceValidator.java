package com.github.mdevloo.multi.tenancy.fwk.auth0.security;

import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

/** Validates that the JWT token contains the intended audience in its claims. */
@AllArgsConstructor
final class AudienceValidator implements OAuth2TokenValidator<Jwt> {
  private final String audience;

  public final OAuth2TokenValidatorResult validate(final Jwt jwt) {
    final OAuth2Error error =
        new OAuth2Error("invalid_token", "The required audience is missing", null);

    if (jwt.getAudience().contains(this.audience)) {
      return OAuth2TokenValidatorResult.success();
    }

    return OAuth2TokenValidatorResult.failure(error);
  }
}
