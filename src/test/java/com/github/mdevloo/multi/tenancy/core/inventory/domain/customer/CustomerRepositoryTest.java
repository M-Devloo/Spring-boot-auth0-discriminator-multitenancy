package com.github.mdevloo.multi.tenancy.core.inventory.domain.customer;

import com.github.mdevloo.multi.tenancy.AbstractIntegrationTest;
import java.util.Optional;
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
        scripts = "classpath:sql/customer.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
    @Sql(
        scripts = "classpath:sql/cleanup.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
})
class CustomerRepositoryTest extends AbstractIntegrationTest {

  private final CustomerRepository customerRepository;

  @Autowired
  CustomerRepositoryTest(
      final CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  @BeforeEach
  void before() {
    this.mockSecurityContext("auth0|55b53f66-6d1e-48b2-a0d2-8444953b202e");
  }

  @Transactional
  @Test
  void findCustomerWhichIsNotMultiTenant() {
    final Optional<Customer> optional = this.customerRepository.findById(
        UUID.fromString("e0638594-be7f-49ab-b7e9-8179f2975145"));
    Assertions.assertThat(optional).isPresent();
    final Customer customer = optional.get();
    customer.getInventory(); // TODO Still a bug in my opinion. When you call a multi tenant entity from a Non Multi tenany entity, it should filter the tenantId anyway!
  }
}
