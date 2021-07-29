package com.github.mdevloo.multi.tenancy.fwk.multitenancy;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Objects;

import static com.github.mdevloo.multi.tenancy.fwk.multitenancy.TenantEntity.TENANT_FILTER_ARGUMENT_NAME;
import static com.github.mdevloo.multi.tenancy.fwk.multitenancy.TenantEntity.TENANT_FILTER_NAME;

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

    final Session session = this.entityManager.unwrap(Session.class);
    final Filter filter =
        session
            .enableFilter(TENANT_FILTER_NAME)
            .setParameter(
                TENANT_FILTER_ARGUMENT_NAME, TenantAssistance.resolveCurrentTenantIdentifier());
    filter.validate();

    if (Objects.isNull(session.getEnabledFilter(TENANT_FILTER_NAME))) {
      throw new UnknownTenantException("Hibernate filter is not enabled");
    }

    return pjp.proceed();
  }
}
