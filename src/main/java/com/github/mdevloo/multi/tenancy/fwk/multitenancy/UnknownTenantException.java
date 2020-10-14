package com.github.mdevloo.multi.tenancy.fwk.multitenancy;

public final class UnknownTenantException extends RuntimeException {

  public UnknownTenantException(final String message) {
    super(message);
  }
}
