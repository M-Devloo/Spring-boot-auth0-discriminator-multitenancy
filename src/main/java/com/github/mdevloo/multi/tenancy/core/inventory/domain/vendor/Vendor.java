package com.github.mdevloo.multi.tenancy.core.inventory.domain.vendor;

import com.github.mdevloo.multi.tenancy.core.inventory.domain.inventory.Inventory;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;

/** Using something different than ID to test the Multi tenancy findById implementation. */
@Entity
@Table(schema = "core", name = "vendor")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class Vendor extends TenantEntity {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(name = "vendor_id", updatable = false, nullable = false)
  private UUID vendorId;

  @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL)
  private List<Inventory> inventory;
}
