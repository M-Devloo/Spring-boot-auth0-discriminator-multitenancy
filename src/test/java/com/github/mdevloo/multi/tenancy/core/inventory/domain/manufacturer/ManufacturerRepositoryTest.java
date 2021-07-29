package com.github.mdevloo.multi.tenancy.core.inventory.domain.manufacturer;

import com.github.mdevloo.multi.tenancy.AbstractIntegrationTest;
import com.github.mdevloo.multi.tenancy.fwk.multitenancy.UnknownTenantException;
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
  ManufacturerRepositoryTest(
      final ManufacturerRepository manufacturerRepository) {
    this.manufacturerRepository = manufacturerRepository;
  }

  @BeforeEach
  void before() {
    this.mockSecurityContext("auth0|99b53f66-6d1e-48b2-a0d2-8444953b202e");
  }

  @Test
  void saveAManufacturerWithNoTenantAndNoNoMultiTenancyAnnotationShouldThrow() {
    final Manufacturer manufacturer1 = new Manufacturer(UUID.randomUUID(), "generated");
    Assertions.assertThatExceptionOfType(UnknownTenantException.class)
        .isThrownBy(() -> this.manufacturerRepository.saveAndFlush(manufacturer1))
        .withMessage("Tenant interceptor did not detect a valid tenant");
  }

  @Test
  void findByIdWithoutNoMultiTenancyAnnotationShouldFailAutomatically() {
    final Manufacturer manufacturer =
        this.manufacturerRepository
            .findById(UUID.fromString("1e23f33e-4668-4e31-a6b5-f165a9c4f591"))
            .orElseThrow();
    Assertions.assertThat(manufacturer.getName()).isEqualTo("Bits");
    manufacturer.setName("hi");
  }
}
