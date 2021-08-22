package com.github.mdevloo.multi.tenancy.fwk.multitenancy;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Lazy(value = false)
@Component
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NoMultiTenancyConfiguration {

  private static final List<Class<?>> NO_MULTI_TENANCY_REPOSITORIES_TYPES;
  private static final String MDEVLOO_MULTI_TENANCY = "com.github.mdevloo.multi.tenancy"; // todo can this be dynamic?

  static {
    final Reflections reflections = new Reflections(MDEVLOO_MULTI_TENANCY);

    NO_MULTI_TENANCY_REPOSITORIES_TYPES =
        reflections.getTypesAnnotatedWith(NoMultiTenancyRepository.class).stream()
            .collect(Collectors.toUnmodifiableList());

    log.warn(
        "Found total of {} repositories that have multi tenancy disabled",
        NO_MULTI_TENANCY_REPOSITORIES_TYPES.size());
    NO_MULTI_TENANCY_REPOSITORIES_TYPES.forEach(
        noMultiTenancy ->
            log.warn(
                "Contains: [{}]: [{}]",
                NoMultiTenancyRepository.class.getSimpleName(),
                noMultiTenancy.getName()));


   /* final List<Class<?>> tenantEntities = reflections.getSubTypesOf(TenantEntity.class).stream().collect(Collectors.toUnmodifiableList());
    final List<Class<?>> allEntities = reflections.getTypesAnnotatedWith(Entity.class).stream().collect(Collectors.toUnmodifiableList());*/

    // todo check if entity has tenantEntities extended OR have the NoMultiTenancy annotation, else fail. (integration test already fails now, but would be nice to show on runtime like hibernate does)
  }

  protected static boolean doesTargetClassInterfaceHasNoMultiTenancyAnnotation(final Object targetClass) {
    final Class<?>[] repositoryInterfaces = targetClass.getClass().getInterfaces();
    for (final Class<?> repositoryInterface : repositoryInterfaces) {
      for (final Class<?> noMultiTenancyRepositoryType : NO_MULTI_TENANCY_REPOSITORIES_TYPES) {
        if (noMultiTenancyRepositoryType == repositoryInterface) {
          return true;
        }
      }
    }

    return false;
  }
}
