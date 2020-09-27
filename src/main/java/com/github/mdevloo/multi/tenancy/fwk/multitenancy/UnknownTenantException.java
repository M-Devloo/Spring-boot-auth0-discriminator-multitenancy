package com.github.mdevloo.multi.tenancy.fwk.multitenancy;

public class UnknownTenantException extends RuntimeException {

  public UnknownTenantException(final String message) {
    super(message);
  }
}
