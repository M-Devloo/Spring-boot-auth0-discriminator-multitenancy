package com.github.mdevloo.multi.tenancy.core.inventory.domain.manufacturer;

import com.github.mdevloo.multi.tenancy.AbstractIntegrationTest;
import com.github.mdevloo.multi.tenancy.fwk.multitenancy.NoMultiTenancy;
import com.github.mdevloo.multi.tenancy.fwk.multitenancy.TenantEntity;
import java.util.Optional;
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
  void saveAManufacturerEntityShouldBeVisibleForAllUsers() {
    final Manufacturer savedManufacturer = this.manufacturerRepository.save(
        new Manufacturer(UUID.randomUUID(), "generated"));
    Assertions.assertThat(savedManufacturer.getName()).isEqualTo("generated");

    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    final Optional<Manufacturer> byId = this.manufacturerRepository.findById(
        savedManufacturer.getId());
    Assertions.assertThat(byId).isPresent();
    Assertions.assertThat(byId.get().getId()).isEqualTo(savedManufacturer.getId());
    Assertions.assertThat(byId.get().getName()).isEqualTo(savedManufacturer.getName());
  }

  @Test
  void manufacturerShouldBeAvailableForAllUsers() {
    this.mockSecurityContext("auth0|88b53f66-6d1e-48b2-a0d2-8444953b202e");
    final String id = "8780d2d8-b637-42bb-8b32-b45fde1712eb";
    Assertions.assertThat(
        this.manufacturerRepository.findById(UUID.fromString(id))).isPresent();
    this.mockSecurityContext("no user even");
    Assertions.assertThat(
        this.manufacturerRepository.findById(UUID.fromString(id))).isPresent();
    this.mockSecurityContext("auth0|99b53f66-6d1e-48b2-a0d2-8444953b202e");
    Assertions.assertThat(
        this.manufacturerRepository.findById(UUID.fromString(id))).isPresent();
  }

  @Test
  void manufacturerShouldNotExtendFromTenantEntity() {
    Assertions.assertThat(Manufacturer.class.isAssignableFrom(TenantEntity.class)).isFalse();
    Assertions.assertThat(Manufacturer.class.isAnnotationPresent(NoMultiTenancy.class)).isTrue();
  }
}
