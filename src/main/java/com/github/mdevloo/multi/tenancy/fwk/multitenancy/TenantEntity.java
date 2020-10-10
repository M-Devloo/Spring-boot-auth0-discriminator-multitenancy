package com.github.mdevloo.multi.tenancy.fwk.multitenancy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@RequiredArgsConstructor
@Getter
@Setter
@MappedSuperclass()
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
}
