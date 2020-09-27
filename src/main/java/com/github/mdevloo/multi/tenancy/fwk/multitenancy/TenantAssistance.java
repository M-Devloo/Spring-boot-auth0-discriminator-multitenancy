package com.github.mdevloo.multi.tenancy.fwk.multitenancy;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TenantAssistance {

  public static String resolveCurrentTenantIdentifier() {
    return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
        .filter(authentication -> authentication instanceof JwtAuthenticationToken)
        .map(authentication -> (JwtAuthenticationToken) authentication)
        .map(JwtAuthenticationToken::getName)
        .orElseThrow(() -> new UnknownTenantException("Tenant is empty"));
  }
}
