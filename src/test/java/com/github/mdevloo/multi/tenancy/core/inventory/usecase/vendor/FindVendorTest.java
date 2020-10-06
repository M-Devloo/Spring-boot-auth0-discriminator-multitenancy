package com.github.mdevloo.multi.tenancy.core.inventory.usecase.vendor;

import com.github.mdevloo.multi.tenancy.AbstractIntegrationTest;
import com.github.mdevloo.multi.tenancy.core.inventory.domain.vendor.Vendor;
import com.github.mdevloo.multi.tenancy.fwk.ObjectNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@SqlGroup({
  @Sql(
      scripts = "classpath:sql/vendor.sql",
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
  @Sql(
      scripts = "classpath:sql/cleanup.sql",
      executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
})
class FindVendorTest extends AbstractIntegrationTest {

  public static final String ID = "850e192b-91ab-4439-8e12-9420a060bbc6";
  public static final String TENANT_ID = "auth0|55b53f66-6d1e-48b2-a0d2-8444953b202e";
  private final FindVendor findVendor;

  @Autowired
  FindVendorTest(final FindVendor findVendor) {
    this.findVendor = findVendor;
  }

  @BeforeEach
  void before() {
    this.mockSecurityContext(TENANT_ID);
  }

  @Transactional
  @Test
  void findVendorById() {
    final Vendor vendor = this.findVendor.execute(UUID.fromString(ID));
    Assertions.assertThat(vendor.getVendorId()).isEqualTo(UUID.fromString(ID));
    Assertions.assertThat(vendor.getTenantId()).isEqualTo(TENANT_ID);
  }

  @Transactional
  @Test
  void findNonExistingVendor() {
    final UUID uuid = UUID.fromString("850e192b-91ab-4439-8e12-9420a060bbc8");
    Assertions.assertThatExceptionOfType(ObjectNotFoundException.class)
        .isThrownBy(() -> this.findVendor.execute(uuid));
  }
}
