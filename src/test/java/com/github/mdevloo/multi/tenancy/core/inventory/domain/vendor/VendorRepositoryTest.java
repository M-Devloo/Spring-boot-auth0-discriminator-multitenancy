package com.github.mdevloo.multi.tenancy.core.inventory.domain.vendor;

import com.github.mdevloo.multi.tenancy.AbstractIntegrationTest;
import com.github.mdevloo.multi.tenancy.core.inventory.domain.inventory.Inventory;
import com.github.mdevloo.multi.tenancy.fwk.multitenancy.TenantEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

class VendorRepositoryTest extends AbstractIntegrationTest {

  private static final String CURRENT_TENANT_VENDOR_ID = "850e192b-91ab-4439-8e12-9420a060bbc8";
  private final VendorRepository vendorRepository;

  @Autowired
  VendorRepositoryTest(final VendorRepository vendorRepository) {
    this.vendorRepository = vendorRepository;
  }

  @BeforeEach
  void before() {
    this.mockSecurityContext("auth0|99b53f66-6d1e-48b2-a0d2-8444953b202e");
  }

  @SqlGroup({
    @Sql(
        scripts = "classpath:sql/vendor_and_inventory.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
    @Sql(
        scripts = "classpath:sql/cleanup.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
  })
  @Test
  void findById() {
    this.transaction(
        () -> {
          final Optional<Vendor> otherTenantVendor =
              this.vendorRepository.findById(
                  UUID.fromString("850e192b-91ab-4439-8e12-9420a060bbc6"));
          Assertions.assertThat(otherTenantVendor).isEmpty();

          final Optional<Vendor> currentTenant =
              this.vendorRepository.findById(UUID.fromString(CURRENT_TENANT_VENDOR_ID));
          Assertions.assertThat(currentTenant).isPresent();
          Assertions.assertThat(currentTenant.get().getVendorId())
              .isEqualTo(UUID.fromString(CURRENT_TENANT_VENDOR_ID));

          final List<Inventory> inventory = currentTenant.get().getInventory();
          Assertions.assertThat(inventory).hasSize(2);
          Assertions.assertThat(inventory.get(0).getName()).isNotNull();
          Assertions.assertThat(inventory.get(0).getTenantId())
              .isEqualTo("auth0|99b53f66-6d1e-48b2-a0d2-8444953b202e");
          Assertions.assertThat(inventory.get(0).getAmount()).isNotNull();
          Assertions.assertThat(inventory.get(0).getId()).isNotNull();
        });
  }

  @SqlGroup({
    @Sql(
        scripts = "classpath:sql/vendor_and_inventory.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
    @Sql(
        scripts = "classpath:sql/cleanup.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
  })
  @Test
  void saveVendorWithInventories() {
    this.transaction(
        () -> {
          final Vendor vendor = new Vendor();
          final Inventory firstInventory = new Inventory();
          firstInventory.setName("name");
          firstInventory.setVendor(vendor);
          firstInventory.setAmount(50);

          final ArrayList<Inventory> inventories = new ArrayList<>();
          inventories.add(firstInventory);
          vendor.setInventory(inventories);

          final Vendor savedVendor = this.vendorRepository.save(vendor);
          Assertions.assertThat(savedVendor.getTenantId())
              .isEqualTo("auth0|99b53f66-6d1e-48b2-a0d2-8444953b202e");
          Assertions.assertThat(savedVendor.getInventory()).hasSize(1);
          final var inventory = savedVendor.getInventory().get(0);
          Assertions.assertThat(inventory.getTenantId()).isEqualTo(vendor.getTenantId());
          Assertions.assertThat(inventory.getAmount()).isEqualTo(firstInventory.getAmount());
          Assertions.assertThat(inventory.getName()).isEqualTo(firstInventory.getName());
        });
  }

  @SqlGroup({
    @Sql(
        scripts = "classpath:sql/vendor_and_inventory.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
    @Sql(
        scripts = "classpath:sql/cleanup.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
  })
  @Test
  void findAllInventoryOfTenant() {
    this.transaction(
        () -> {
          Assertions.assertThat(this.vendorRepository.findAll()).hasSize(1);

          this.mockSecurityContext("auth0|55b53f66-6d1e-48b2-a0d2-8444953b202e");
          final var otherTenantSwitchEvents = this.vendorRepository.findAll();
          Assertions.assertThat(otherTenantSwitchEvents).hasSize(2);
        });
  }

  @SqlGroup({
    @Sql(
        scripts = "classpath:sql/vendor_and_inventory_faulty_tenant_relation.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
    @Sql(
        scripts = "classpath:sql/cleanup.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
  })
  @Test // todo check why this not fails when running standalone but fails when running multiple.
  void findWrongMigratedInventoriesOfTenant() {
    this.mockSecurityContext("auth0|55b53f66-6d1e-48b2-a0d2-8444953b202e");
    this.transaction(
        () -> {
          final Vendor vendorOfTenant =
              this.vendorRepository
                  .findById(UUID.fromString("850e192b-91ab-4439-8e12-9420a060b999"))
                  .orElseThrow();
          Assertions.assertThat(vendorOfTenant.getTenantId())
              .isEqualTo("auth0|55b53f66-6d1e-48b2-a0d2-8444953b202e");

          Assertions.assertThatExceptionOfType(AccessDeniedException.class)
              .isThrownBy(
                  () -> {
                    final List<Inventory> inventoryOfOtherTenant = vendorOfTenant.getInventory();
                    final List<String> tenantIds =
                        inventoryOfOtherTenant.stream()
                            .map(TenantEntity::getTenantId)
                            .collect(Collectors.toUnmodifiableList());
                  });
        });
  }
}
