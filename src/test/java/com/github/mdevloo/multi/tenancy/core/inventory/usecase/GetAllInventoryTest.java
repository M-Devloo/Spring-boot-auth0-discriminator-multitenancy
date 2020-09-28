package com.github.mdevloo.multi.tenancy.core.inventory.usecase;

import com.github.mdevloo.multi.tenancy.AbstractIntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;

@SqlGroup({
  @Sql(
      scripts = "classpath:sql/inventory.sql",
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
  @Sql(
      scripts = "classpath:sql/cleanup.sql",
      executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
})
class GetAllInventoryTest extends AbstractIntegrationTest {

  private final GetAllInventory getAllInventory;

  @Autowired
  GetAllInventoryTest(final GetAllInventory getAllInventory) {
    this.getAllInventory = getAllInventory;
  }

  @Transactional
  @Test
  void getAllInventoryOfCurrentTenant() {
    this.mockSecurityContext("non-existing-tenant");
    Assertions.assertThat(this.getAllInventory.execute()).isEmpty();
    this.mockSecurityContext("auth0|55b53f66-6d1e-48b2-a0d2-8444953b202e");
    Assertions.assertThat(this.getAllInventory.execute()).hasSize(2);
    this.mockSecurityContext("auth0|99b53f66-6d1e-48b2-a0d2-8444953b202e");
    Assertions.assertThat(this.getAllInventory.execute()).hasSize(2);
    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    Assertions.assertThat(this.getAllInventory.execute()).hasSize(1);
  }
}
