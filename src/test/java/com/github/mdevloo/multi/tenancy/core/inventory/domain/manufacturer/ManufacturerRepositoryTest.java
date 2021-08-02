package com.github.mdevloo.multi.tenancy.core.inventory.domain.manufacturer;

import com.github.mdevloo.multi.tenancy.AbstractIntegrationTest;
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

  @Autowired
  ManufacturerRepositoryTest(final ManufacturerRepository manufacturerRepository) {
    this.manufacturerRepository = manufacturerRepository;
  }

  @BeforeEach
  void before() {
    this.mockSecurityContext("auth0|99b53f66-6d1e-48b2-a0d2-8444953b202e");
  }

  @Test
  void saveAManufacturerWithWithNoTenantAnnotationShouldSaveWithoutTenant() {
    final Manufacturer manufacturer = new Manufacturer(UUID.randomUUID(), "generated");
    final Manufacturer savedManufacturer = this.manufacturerRepository.saveAndFlush(manufacturer);
    Assertions.assertThat(savedManufacturer.getName()).isEqualTo("generated");
  }

  @Test
  void findByIdWithNoMultiTenancyAnnotationShouldWorkWithoutTenant() {
    final UUID id = UUID.fromString("1e23f33e-4668-4e31-a6b5-f165a9c4f591");
    final Manufacturer manufacturer = this.manufacturerRepository.findById(id).orElseThrow();
    Assertions.assertThat(manufacturer.getId()).isEqualTo(id);
  }

  /**
   * TODO: Write Spring AOP test to verify if @NoMultiTenancyRepository and @NoMultiTenancy annotation is used together + test edge cases.
   * TODO: Create 4th entity without the @MultiTenancy annotation and write validations for that as well.
   */
}
