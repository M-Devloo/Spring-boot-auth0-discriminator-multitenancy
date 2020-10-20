package com.github.mdevloo.multi.tenancy.fwk.multitenancy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.springframework.security.access.AccessDeniedException;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import java.util.Objects;

@RequiredArgsConstructor
@Getter
@Setter
@MappedSuperclass
@FilterDef(
    name = TenantEntity.TENANT_FILTER_NAME,
    parameters = @ParamDef(name = TenantEntity.TENANT_FILTER_ARGUMENT_NAME, type = "string"),
    defaultCondition =
        TenantEntity.TENANT_ID_PROPERTY_NAME + "= :" + TenantEntity.TENANT_FILTER_ARGUMENT_NAME)
@Filter(name = TenantEntity.TENANT_FILTER_NAME)
public class TenantEntity {
  static final String TENANT_FILTER_NAME = "tenantFilter";
  static final String TENANT_ID_PROPERTY_NAME = "tenant_id";
  static final String TENANT_FILTER_ARGUMENT_NAME = "tenantId";

  @Column(name = TENANT_ID_PROPERTY_NAME, nullable = false)
  String tenantId;

  /**
   * Hibernate filter blocks if the entity is not owned by the tenant. But this is not the case when
   * a entity has a wrong relation. After loading the entity, it checks that the object is owned by
   * the current tenant. This is required for Collections & protects your entities from faulty
   * migrations.
   */
  @PostLoad
  public void postLoad() {
    if (!this.isNewEntity()
        && !TenantAssistance.resolveCurrentTenantIdentifier().equals(this.tenantId)) {
      throw new AccessDeniedException(
          "Not allowed to load this object as it is not of the current tenant.");
    }
  }

  private boolean isNewEntity() {
    return Objects.isNull(this.tenantId);
  }
}
