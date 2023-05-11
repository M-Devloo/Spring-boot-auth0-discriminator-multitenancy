package com.github.mdevloo.multi.tenancy.core.inventory.domain.inventory;

import com.github.mdevloo.multi.tenancy.core.inventory.domain.vendor.Vendor;
import com.github.mdevloo.multi.tenancy.fwk.multitenancy.TenantEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(schema = "core", name = "inventory")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class Inventory extends TenantEntity {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @Column(name = "name")
  private String name;

  @Column(name = "amount")
  private Integer amount;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "vendor_id")
  private Vendor vendor;
}
