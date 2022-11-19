package com.github.mdevloo.multi.tenancy.core.inventory.domain.customer;

import com.github.mdevloo.multi.tenancy.core.inventory.domain.inventory.Inventory;
import com.github.mdevloo.multi.tenancy.fwk.multitenancy.NoMultiTenancy;
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(schema = "core", name = "customer")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@NoMultiTenancy
public class Customer {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @Column(name = "name")
  private String name;

  // Warning: MultiTenant object!
  @OneToMany(mappedBy = "customer")
  private List<Inventory> inventory;
}
