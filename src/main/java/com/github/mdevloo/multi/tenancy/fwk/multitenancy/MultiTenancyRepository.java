package com.github.mdevloo.multi.tenancy.fwk.multitenancy;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.lang.NonNull;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
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
    final Class<T> entityType = this.getDomainClass();
    Objects.requireNonNull(id, "ID can not be null for Type [" + entityType + "]");

    final CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
    final CriteriaQuery<T> cq = criteriaBuilder.createQuery(entityType);

    cq.where(
        criteriaBuilder.equal(
            cq.from(entityType).get(this.entityInformation.getRequiredIdAttribute().getName()),
            id));
    final TypedQuery<T> q = this.entityManager.createQuery(cq);

    return q.getResultStream().findFirst();
  }

  @Deprecated
  @Override
  public T getOne(@NonNull final I i) {
    return this.getReferenceById(i);
  }

  @Override
  @Deprecated
  public T getById(@NonNull final I i) {
    return this.getReferenceById(i);
  }

  /**
   * getReferenceById is not multi tenant compliant by default as it returns a lazy fetched proxy of the
   * entity. When executing a getter method, the data is fetched from the database without passing
   * the hibernate filter! JPA allows us to throw EntityNotFoundException and that is exactly what
   * we do here to avoid security holes.
   *
   * @since Spring boot 2.7.5 added
   */
  @Override
  public T getReferenceById(@NonNull final I i) {
    throw new EntityNotFoundException("Lazy fetching is not supported as it breaks multi tenancy");
  }

  @Override
  public void delete(@NonNull final T entity) {
    Objects.requireNonNull(
        entity, "Entity can not be null for Object [" + this.getDomainClass() + "]");

    if (this.entityInformation.isNew(entity)
        || this.findById(this.entityInformation.getRequiredId(entity)).isEmpty()) {
      return;
    }

    this.entityManager.remove(
        this.entityManager.contains(entity) ? entity : this.entityManager.merge(entity));
  }
}
