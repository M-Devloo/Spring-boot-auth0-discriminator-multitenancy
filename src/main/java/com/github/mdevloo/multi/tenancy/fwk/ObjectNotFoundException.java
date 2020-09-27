package com.github.mdevloo.multi.tenancy.fwk;

import java.util.UUID;

public class ObjectNotFoundException extends RuntimeException {

  public ObjectNotFoundException(final Class<?> object, final UUID id) {
    this(object, id.toString());
  }

  public ObjectNotFoundException(final Class<?> object, final String id) {
    super(object.getSimpleName() + " is not found for ID: (" + id + ")");
  }
}
