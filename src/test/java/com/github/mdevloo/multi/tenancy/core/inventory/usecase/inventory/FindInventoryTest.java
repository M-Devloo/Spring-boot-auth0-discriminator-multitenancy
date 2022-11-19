package com.github.mdevloo.multi.tenancy.core.inventory.usecase.inventory;

import com.github.mdevloo.multi.tenancy.AbstractIntegrationTest;
import com.github.mdevloo.multi.tenancy.core.inventory.domain.inventory.Inventory;
import com.github.mdevloo.multi.tenancy.fwk.ObjectNotFoundException;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;

@SqlGroup({
  @Sql(
      scripts = "classpath:sql/manufacturer.sql",
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
  @Sql(
      scripts = "classpath:sql/inventory.sql",
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
  @Sql(
      scripts = "classpath:sql/cleanup.sql",
      executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
})
class FindInventoryTest extends AbstractIntegrationTest {

  private final FindInventory findInventory;

  @Autowired
  FindInventoryTest(final FindInventory findInventory) {
    this.findInventory = findInventory;
  }

  @BeforeEach
  void before() {
    this.mockSecurityContext("auth0|99b53f66-6d1e-48b2-a0d2-8444953b202e");
  }

  @Transactional
  @Test
  void findExistingInventory() {
    final UUID uuid = UUID.fromString("da05d535-a53a-4a30-a8e6-9ede533d25c6");
    final Inventory execute = this.findInventory.execute(uuid);
    Assertions.assertThat(execute.getAmount()).isEqualTo(2);
    Assertions.assertThat(execute.getName()).isEqualTo("Atari 2600");
    Assertions.assertThat(execute.getTenantId())
        .isEqualTo("auth0|99b53f66-6d1e-48b2-a0d2-8444953b202e");
    Assertions.assertThat(execute.getId())
        .isEqualTo(UUID.fromString("da05d535-a53a-4a30-a8e6-9ede533d25c6"));
  }

  @Transactional
  @Test
  void findNonExistingInventory() {
    final UUID uuid = UUID.fromString("aa05d535-a53a-4a30-a8e6-9ede533d25c5");
    Assertions.assertThatExceptionOfType(ObjectNotFoundException.class)
        .isThrownBy(() -> this.findInventory.execute(uuid));
  }
}
