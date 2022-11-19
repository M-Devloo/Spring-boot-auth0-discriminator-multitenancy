package com.github.mdevloo.multi.tenancy.core.inventory.domain.product;

import com.github.mdevloo.multi.tenancy.AbstractIntegrationTest;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

@SqlGroup({
  @Sql(
      scripts = "classpath:sql/cleanup.sql",
      executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
})
class InvalidProductRepositoryTest extends AbstractIntegrationTest {

  private final InvalidProductRepository invalidProductRepository;

  @Autowired
  InvalidProductRepositoryTest(final InvalidProductRepository invalidProductRepository) {
    this.invalidProductRepository = invalidProductRepository;
  }

  @BeforeEach
  void before() {
    this.mockSecurityContext("auth0|99b53f66-6d1e-48b2-a0d2-8444953b202e");
  }

  @Test
  void invalidProductHasNoMultiTenancyAnnotationAndExtendsTenantEntity() {
    Assertions.assertThatExceptionOfType(InvalidDataAccessApiUsageException.class)
        .isThrownBy(
            () ->
                this.invalidProductRepository.saveAndFlush(
                    new InvalidProduct(UUID.randomUUID(), "description")))
        .withCauseExactlyInstanceOf(IllegalArgumentException.class)
        .withMessageContaining(
            "NoMultiTenancy annotation can not be used in combination of TenantEntity");
  }
}
