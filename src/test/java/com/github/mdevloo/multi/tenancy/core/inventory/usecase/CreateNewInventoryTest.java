package com.github.mdevloo.multi.tenancy.core.inventory.usecase;

import com.github.mdevloo.multi.tenancy.AbstractIntegrationTest;
import com.github.mdevloo.multi.tenancy.core.inventory.domain.Inventory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CreateNewInventoryTest extends AbstractIntegrationTest {

  private final CreateNewInventory createNewInventory;

  @Autowired
  CreateNewInventoryTest(final CreateNewInventory createNewInventory) {
    this.createNewInventory = createNewInventory;
  }

  @BeforeEach
  void before() {
    this.mockSecurityContext("auth0|random-tenant-id");
  }

  @Test
  void createNewInventory() {
    final InventoryCreationRequest inventoryCreationRequest =
        new InventoryCreationRequest("Nintendo Switch", 5);
    final Inventory addedInventory = this.createNewInventory.execute(inventoryCreationRequest);
    Assertions.assertThat(addedInventory.getAmount()).isEqualTo(5);
    Assertions.assertThat(addedInventory.getId()).isNotNull();
    Assertions.assertThat(addedInventory.getName()).isEqualTo("Nintendo Switch");
    Assertions.assertThat(addedInventory.getTenantId()).isEqualTo("auth0|random-tenant-id");
  }
}
