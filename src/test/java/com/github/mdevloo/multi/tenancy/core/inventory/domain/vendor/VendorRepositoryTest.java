package com.github.mdevloo.multi.tenancy.core.inventory.domain.vendor;

import com.github.mdevloo.multi.tenancy.AbstractIntegrationTest;
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
      scripts = "classpath:sql/vendor.sql",
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
  @Sql(
      scripts = "classpath:sql/cleanup.sql",
      executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
})
class VendorRepositoryTest extends AbstractIntegrationTest {

  private static final String CURRENT_TENANT_INVENTORY_ID = "850e192b-91ab-4439-8e12-9420a060bbc8";
  private final VendorRepository vendorRepository;

  @Autowired
  VendorRepositoryTest(final VendorRepository vendorRepository) {
    this.vendorRepository = vendorRepository;
  }

  @BeforeEach
  void before() {
    this.mockSecurityContext("auth0|99b53f66-6d1e-48b2-a0d2-8444953b202e");
  }

  @Test
  void findById() {
    this.transaction(
        () -> {
          final Optional<Vendor> otherTenantVendor =
              this.vendorRepository.findById(
                  UUID.fromString("850e192b-91ab-4439-8e12-9420a060bbc6"));
          Assertions.assertThat(otherTenantVendor).isEmpty();

          final Optional<Vendor> currentTenant =
              this.vendorRepository.findById(UUID.fromString(CURRENT_TENANT_INVENTORY_ID));
          Assertions.assertThat(currentTenant).isPresent();
          Assertions.assertThat(currentTenant.get().getVendorId())
              .isEqualTo(UUID.fromString(CURRENT_TENANT_INVENTORY_ID));
        });
  }

  @Test
  void findAllInventoryOfTenant() {
    this.transaction(
        () -> {
          Assertions.assertThat(this.vendorRepository.findAll()).hasSize(1);

          this.mockSecurityContext("auth0|55b53f66-6d1e-48b2-a0d2-8444953b202e");
          final List<Vendor> otherTenantSwitchEvents = this.vendorRepository.findAll();
          Assertions.assertThat(otherTenantSwitchEvents).hasSize(2);
        });
  }
}
