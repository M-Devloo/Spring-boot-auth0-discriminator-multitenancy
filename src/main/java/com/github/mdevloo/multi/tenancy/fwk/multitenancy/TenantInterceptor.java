package com.github.mdevloo.multi.tenancy.fwk.multitenancy;

import static com.github.mdevloo.multi.tenancy.fwk.multitenancy.TenantEntity.TENANT_FILTER_ARGUMENT_NAME;

import java.io.Serializable;
import org.hibernate.Interceptor;
import org.hibernate.type.Type;

public final class TenantInterceptor implements Interceptor {

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
    if (entity instanceof TenantEntity) {
      for (int index = 0; index < propertyName.length; index++) {
        if (propertyName[index].equals(TENANT_FILTER_ARGUMENT_NAME)) {
          state[index] = TenantAssistance.resolveCurrentTenantIdentifier();
          return true;
        }
      }
      throw new ClassCastException();
    }
    return false;
  }
}
