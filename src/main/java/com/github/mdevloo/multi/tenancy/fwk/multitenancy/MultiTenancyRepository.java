package com.github.mdevloo.multi.tenancy.fwk.multitenancy;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.lang.NonNull;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.Objects;
import java.util.Optional;

public class MultiTenancyRepository<T, I> extends SimpleJpaRepository<T, I> {

  private final JpaEntityInformation<T, I> entityInformation;
  private final EntityManager entityManager;

  public MultiTenancyRepository(
      final JpaEntityInformation<T, I> entityInformation, final EntityManager entityManager) {
    super(entityInformation, entityManager);
    this.entityInformation = entityInformation;
    this.entityManager = entityManager;
  }

  @Override
  public Optional<T> findById(@NonNull final I id) {
    final Class<T> entityType = this.entityInformation.getJavaType();
    Objects.requireNonNull(id, "ID can not be null for Type [" + entityType + "]");

    final CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
    final CriteriaQuery<T> cq = criteriaBuilder.createQuery(entityType);

    cq.where(criteriaBuilder.equal(cq.from(entityType).get(this.getId()), id));
    final TypedQuery<T> q = this.entityManager.createQuery(cq);

    return q.getResultStream().findFirst();
  }

  private String getId() {
    return this.entityInformation.getRequiredIdAttribute().getName();
  }
}
