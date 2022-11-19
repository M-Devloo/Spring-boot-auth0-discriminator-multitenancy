package com.github.mdevloo.multi.tenancy.fwk.multitenancy;

import static com.github.mdevloo.multi.tenancy.fwk.multitenancy.TenantEntity.TENANT_FILTER_ARGUMENT_NAME;
import static com.github.mdevloo.multi.tenancy.fwk.multitenancy.TenantEntity.TENANT_FILTER_NAME;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class TenantServiceAspect {

  @PersistenceContext public EntityManager entityManager;

  @Pointcut("execution(public * org.springframework.data.repository.Repository+.*(..))")
  void isRepository() {
    /* aspect */
  }

  @Pointcut(value = "isRepository()")
  void enableMultiTenancy() {
    /* aspect */
  }

  @Around("execution(public * *(..)) && enableMultiTenancy()")
  public Object aroundExecution(final ProceedingJoinPoint pjp) throws Throwable {

    if (!this.doesAnyRepositoryTypeArgumentHaveDisabledMultiTenancy(pjp)) {
      final Session session = this.entityManager.unwrap(Session.class);
      final Filter filter = session // requires transaction
          .enableFilter(TENANT_FILTER_NAME)
          .setParameter(
              TENANT_FILTER_ARGUMENT_NAME, TenantAssistance.resolveCurrentTenantIdentifier());

      if (Objects.isNull(session.getEnabledFilter(TENANT_FILTER_NAME))) {
        throw new UnknownTenantException("Hibernate filter is not enabled");
      }
      filter.validate();
    }

    return pjp.proceed();
  }

  private boolean doesAnyRepositoryTypeArgumentHaveDisabledMultiTenancy(final ProceedingJoinPoint pjp) {
    final List<Type> repositoryTypeArguments = Optional.of(pjp.getTarget())
        .map(Object::getClass)
        .map(Class::getInterfaces)
        .map(List::of)
        .orElse(Collections.emptyList())
        .stream().map(Class::getAnnotatedInterfaces)
        .flatMap(jjj -> Arrays.stream(jjj).map(AnnotatedType::getType))
        .filter(ParameterizedType.class::isInstance)
        .map(ParameterizedType.class::cast)
        .map(parameterizedType -> List.of(parameterizedType.getActualTypeArguments()))
        .flatMap(List::stream)
        .collect(Collectors.toList());

    for (final Type type : repositoryTypeArguments) {
      if (MultiTenancyAssistance.entityNotMultiTenant(type)) {
        return true;
      }
    }

    return false;
  }
}
