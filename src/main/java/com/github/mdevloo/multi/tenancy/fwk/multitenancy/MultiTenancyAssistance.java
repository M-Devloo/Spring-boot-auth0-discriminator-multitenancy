package com.github.mdevloo.multi.tenancy.fwk.multitenancy;

import java.util.Arrays;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class MultiTenancyAssistance {

  static boolean isMultiTenancyEntity(final Object entityObject) {
    final Optional<NoMultiTenancy> noMultiTenancy = findAnnotation(entityObject);
    if (noMultiTenancy.isPresent() && entityObject instanceof TenantEntity) {
      throw new IllegalArgumentException(
          "NoMultiTenancy annotation can not be used in combination of "
              + TenantEntity.class.getSimpleName());
    }

    return noMultiTenancy.isEmpty();
  }

  private static Optional<NoMultiTenancy> findAnnotation(final Object entity) {
    final Class<?> entityClass = entity instanceof Class<?> ? (Class<?>) (entity) : entity.getClass();
    return Arrays.stream(entityClass.getAnnotations())
        .filter(NoMultiTenancy.class::isInstance)
        .map(NoMultiTenancy.class::cast)
        .findFirst();
  }
}
