package com.github.mdevloo.multi.tenancy.core.inventory.domain;

import com.github.mdevloo.multi.tenancy.AbstractIntegrationTest;
import com.github.mdevloo.multi.tenancy.core.inventory.domain.inventory.Inventory;
import com.github.mdevloo.multi.tenancy.core.inventory.domain.inventory.InventoryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SqlGroup({
  @Sql(
      scripts = "classpath:sql/inventory.sql",
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
  @Sql(
      scripts = "classpath:sql/cleanup.sql",
      executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
})
class InventoryRepositoryTest extends AbstractIntegrationTest {

  public static final String CURRENT_TENANT_INVENTORY_ID = "aa05d535-a53a-4a30-a8e6-9ede533d25c5";
  private final InventoryRepository inventoryRepository;

  @Autowired
  InventoryRepositoryTest(final InventoryRepository inventoryRepository) {
    this.inventoryRepository = inventoryRepository;
  }

  @BeforeEach
  void before() {
    this.mockSecurityContext("auth0|55b53f66-6d1e-48b2-a0d2-8444953b202e");
  }

  @Test
  void findById() {
    this.transaction(
        () -> {
          final Optional<Inventory> otherTenantInventory =
              this.inventoryRepository.findById(
                  UUID.fromString("ca05d535-a53a-4a30-a8e6-9ede533d25c6"));
          Assertions.assertThat(otherTenantInventory).isEmpty();

          final Optional<Inventory> currentTenant =
              this.inventoryRepository.findById(UUID.fromString(CURRENT_TENANT_INVENTORY_ID));
          Assertions.assertThat(currentTenant).isPresent();
          Assertions.assertThat(currentTenant.get().getId())
              .isEqualTo(UUID.fromString(CURRENT_TENANT_INVENTORY_ID));
        });
  }

  @Test
  void findAllInventoryOfTenant() {
    this.transaction(
        () -> {
          Assertions.assertThat(this.inventoryRepository.findAll()).hasSize(2);

          this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
          final List<Inventory> otherTenantSwitchEvents = this.inventoryRepository.findAll();
          Assertions.assertThat(otherTenantSwitchEvents).hasSize(1);
          Assertions.assertThat(otherTenantSwitchEvents.get(0).getName())
              .isEqualTo("Sega Master System");
        });
  }
}
