package com.github.mdevloo.multi.tenancy.fwk.multitenancy;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

import static com.github.mdevloo.multi.tenancy.fwk.multitenancy.TenantEntity.TENANT_FILTER_ARGUMENT_NAME;

public final class TenantInterceptor extends EmptyInterceptor {

  @Override
  public boolean onSave(
      final Object entity,
      final Serializable id,
      final Object[] state,
      final String[] propertyNames,
      final Type[] types) {
    return this.addTenantIdIfObjectIsTenantEntity(entity, state, propertyNames);
  }

  @Override
  public boolean onFlushDirty(
      final Object entity,
      final Serializable id,
      final Object[] currentState,
      final Object[] previousState,
      final String[] propertyNames,
      final Type[] types) {
    return this.addTenantIdIfObjectIsTenantEntity(entity, currentState, propertyNames);
  }

  @Override
  public void onDelete(
      final Object entity,
      final Serializable id,
      final Object[] state,
      final String[] propertyNames,
      final Type[] types) {
    this.addTenantIdIfObjectIsTenantEntity(entity, state, propertyNames);
  }

  private boolean addTenantIdIfObjectIsTenantEntity(
      Object entity, Object[] state, String[] propertyName) {

    final Optional<NoMultiTenancy> noMultiTenancy = this.findAnnotation(entity);
    if (noMultiTenancy.isPresent() && entity instanceof TenantEntity) {
      throw new IllegalArgumentException(
          "NoMultiTenancy annotation can not be used in combination of TenantEntity");
    }

    if (noMultiTenancy.isEmpty() && entity instanceof TenantEntity) {
      for (int index = 0; index < propertyName.length; index++) {
        if (propertyName[index].equals(TENANT_FILTER_ARGUMENT_NAME)) {
          state[index] = TenantAssistance.resolveCurrentTenantIdentifier();
          return true;
        }
      }
    }

    if (noMultiTenancy.isPresent()) {
      return true;
    }

    throw new UnknownTenantException("Tenant interceptor did not detect a valid tenant");
  }

  private Optional<NoMultiTenancy> findAnnotation(final Object entity) {
    return Arrays.stream(entity.getClass().getAnnotations())
        .filter(NoMultiTenancy.class::isInstance)
        .map(NoMultiTenancy.class::cast)
        .findFirst();
  }
}
