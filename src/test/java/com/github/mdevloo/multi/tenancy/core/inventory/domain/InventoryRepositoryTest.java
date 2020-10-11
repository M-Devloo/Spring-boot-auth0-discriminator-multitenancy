package com.github.mdevloo.multi.tenancy.core.inventory.domain;

import com.github.mdevloo.multi.tenancy.AbstractIntegrationTest;
import com.github.mdevloo.multi.tenancy.core.inventory.domain.inventory.Inventory;
import com.github.mdevloo.multi.tenancy.core.inventory.domain.inventory.InventoryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;

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

  @Transactional
  @Test
  void deleteById() {
    final UUID otherTenantId = UUID.fromString("ea05d535-a53a-4a30-a8e6-9ede533d25c6");
    Assertions.assertThatExceptionOfType(EmptyResultDataAccessException.class)
        .isThrownBy(() -> this.inventoryRepository.deleteById(otherTenantId));
  }

  @Transactional // Bug See issue #4
  @Test
  void delete() {
    final Inventory inventory = new Inventory();
    inventory.setId(UUID.fromString("ea05d535-a53a-4a30-a8e6-9ede533d25c6"));
    Assertions.assertThatExceptionOfType(EmptyResultDataAccessException.class)
        .isThrownBy(() -> this.inventoryRepository.delete(inventory));
  }

  @Transactional // Bug See issue #4
  @Test
  void deleteAllWithObjectList() {
    this.delete(
        () -> {
          this.inventoryRepository.deleteAll(this.otherTenantsInventory());
          Assertions.assertThat(this.inventoryRepository.findAll()).hasSize(2);
        });
  }

  @Transactional
  @Test
  void deleteInBatch() {
    this.delete(
        () -> {
          this.inventoryRepository.deleteInBatch(this.otherTenantsInventory());
          Assertions.assertThat(this.inventoryRepository.findAll()).hasSize(2);
        });
  }

  @Transactional
  @Test
  void deleteAll() {
    this.delete(
        () -> {
          this.inventoryRepository.deleteAll();
          Assertions.assertThat(this.inventoryRepository.findAll()).isEmpty();
        });
  }

  @Transactional
  @Test
  void deleteAllInBatch() {
    this.delete(
        () -> {
          this.inventoryRepository.deleteAllInBatch();
          Assertions.assertThat(this.inventoryRepository.findAll()).isEmpty();
        });
  }

  @Transactional
  @Test
  void findById() {
    final Optional<Inventory> otherTenantInventory =
        this.inventoryRepository.findById(UUID.fromString("ca05d535-a53a-4a30-a8e6-9ede533d25c6"));
    Assertions.assertThat(otherTenantInventory).isEmpty();

    final Optional<Inventory> currentTenant =
        this.inventoryRepository.findById(UUID.fromString(CURRENT_TENANT_INVENTORY_ID));
    Assertions.assertThat(currentTenant).isPresent();
    Assertions.assertThat(currentTenant.get().getId())
        .isEqualTo(UUID.fromString(CURRENT_TENANT_INVENTORY_ID));
  }

  @Transactional // Bug See issue #4
  @Test
  void getOne() {
    Assertions.assertThatIllegalArgumentException()
        .isThrownBy(
            () ->
                this.inventoryRepository.getOne(
                    UUID.fromString("ca05d535-a53a-4a30-a8e6-9ede533d25c6")));

    final Inventory currentTenant =
        this.inventoryRepository.getOne(UUID.fromString(CURRENT_TENANT_INVENTORY_ID));
    Assertions.assertThat(currentTenant.getId())
        .isEqualTo(UUID.fromString(CURRENT_TENANT_INVENTORY_ID));
  }

  @Transactional
  @Test
  void existsById() {
    Assertions.assertThat(
            this.inventoryRepository.existsById(
                UUID.fromString("ca05d535-a53a-4a30-a8e6-9ede533d25c6")))
        .isFalse();
    Assertions.assertThat(
            this.inventoryRepository.existsById(UUID.fromString(CURRENT_TENANT_INVENTORY_ID)))
        .isTrue();
  }

  @Transactional
  @Test
  void findAll() {
    Assertions.assertThat(this.inventoryRepository.findAll()).hasSize(2);

    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    final List<Inventory> otherTenantSwitchEvents = this.inventoryRepository.findAll();
    Assertions.assertThat(otherTenantSwitchEvents).hasSize(1);
    Assertions.assertThat(otherTenantSwitchEvents.get(0).getName()).isEqualTo("Sega Master System");
  }

  @Transactional
  @Test
  void findAllById() {
    Assertions.assertThat(
            this.inventoryRepository.findAllById(
                List.of(UUID.fromString("ca05d535-a53a-4a30-a8e6-9ede533d25c6"))))
        .isEmpty();

    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    final List<Inventory> otherTenantSwitchEvents =
        this.inventoryRepository.findAllById(
            List.of(UUID.fromString("ea05d535-a53a-4a30-a8e6-9ede533d25c6")));
    Assertions.assertThat(otherTenantSwitchEvents).hasSize(1);
    Assertions.assertThat(otherTenantSwitchEvents.get(0).getName()).isEqualTo("Sega Master System");
  }

  @Transactional
  @Test
  void findAllWithSort() {
    Assertions.assertThat(this.inventoryRepository.findAll(Sort.unsorted())).hasSize(2);

    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    final List<Inventory> otherTenantSwitchEvents =
        this.inventoryRepository.findAll(Sort.unsorted());
    Assertions.assertThat(otherTenantSwitchEvents).hasSize(1);
    Assertions.assertThat(otherTenantSwitchEvents.get(0).getName()).isEqualTo("Sega Master System");
  }

  @Transactional
  @Test
  void findAllWithPageable() {
    Assertions.assertThat(this.inventoryRepository.findAll(Pageable.unpaged()).getTotalElements())
        .isEqualTo(2);

    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    final Page<Inventory> otherTenantSwitchEvents =
        this.inventoryRepository.findAll(Pageable.unpaged());

    Assertions.assertThat(otherTenantSwitchEvents.getTotalElements()).isEqualTo(1);
    Assertions.assertThat(otherTenantSwitchEvents.getContent().get(0).getName())
        .isEqualTo("Sega Master System");
  }

  // todo continue with findOne(@Nullable Specification<T> spec)

  /** Object of tenant: auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e */
  private List<Inventory> otherTenantsInventory() {
    final Inventory otherTenantInventory = new Inventory();
    otherTenantInventory.setId(UUID.fromString("ea05d535-a53a-4a30-a8e6-9ede533d25c6"));
    return List.of(otherTenantInventory);
  }

  private void delete(final Runnable runnable) {
    Assertions.assertThat(this.inventoryRepository.findAll()).hasSize(2);

    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    Assertions.assertThat(this.inventoryRepository.findAll()).hasSize(1);

    this.mockSecurityContext("auth0|55b53f66-6d1e-48b2-a0d2-8444953b202e");
    runnable.run();

    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    final List<Inventory> inventoryOfOtherTenant = this.inventoryRepository.findAll();
    Assertions.assertThat(inventoryOfOtherTenant).hasSize(1);
    Assertions.assertThat(inventoryOfOtherTenant.get(0).getId())
        .isEqualTo(this.otherTenantsInventory().get(0).getId());
  }
}
