package com.github.mdevloo.multi.tenancy.core.inventory.domain.manufacturer;

import com.github.mdevloo.multi.tenancy.AbstractIntegrationTest;
import com.github.mdevloo.multi.tenancy.core.inventory.domain.vendor.VendorRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.UUID;

@SqlGroup({
  @Sql(
      scripts = "classpath:sql/manufacturer.sql",
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
  @Sql(
      scripts = "classpath:sql/cleanup.sql",
      executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
})
class ManufacturerRepositoryTest extends AbstractIntegrationTest {

  private final ManufacturerRepository manufacturerRepository;
  private final VendorRepository vendorRepository;

  @Autowired
  ManufacturerRepositoryTest(
      final ManufacturerRepository manufacturerRepository,
      final VendorRepository vendorRepository) {
    this.manufacturerRepository = manufacturerRepository;
    this.vendorRepository = vendorRepository;
  }

  @BeforeEach
  void before() {
    this.mockSecurityContext("auth0|99b53f66-6d1e-48b2-a0d2-8444953b202e");
  }

  @Test
  void findById() {
    final Manufacturer manufacturer1 = new Manufacturer(UUID.randomUUID(), "generated");
    this.manufacturerRepository.saveAndFlush(manufacturer1);

    final Manufacturer manufacturer =
        this.manufacturerRepository
            .findById(UUID.fromString("1e23f33e-4668-4e31-a6b5-f165a9c4f591"))
            .orElseThrow();
    Assertions.assertThat(manufacturer.getName()).isEqualTo("Bits");
    manufacturer.setName("hi");
  }
}
