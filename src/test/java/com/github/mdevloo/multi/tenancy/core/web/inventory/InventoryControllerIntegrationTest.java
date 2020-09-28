package com.github.mdevloo.multi.tenancy.core.web.inventory;

import static com.github.mdevloo.multi.tenancy.core.AppConstants.INVENTORY_URL;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.mdevloo.multi.tenancy.core.AbstractMockMvcTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

class InventoryControllerIntegrationTest extends AbstractMockMvcTest {

  @SqlGroup({
    @Sql(
        scripts = "classpath:sql/inventory.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
    @Sql(
        scripts = "classpath:sql/cleanup.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
  })
  @Test
  void getInventoryHappyPath() throws Exception {
    this.assertResponse(
        this.getMockMvc()
            .perform(
                get(INVENTORY_URL)
                    .with(jwt().jwt(this.getJwtToken("auth0|55b53f66-6d1e-48b2-a0d2-8444953b202e")))
                    .param("id", "aa05d535-a53a-4a30-a8e6-9ede533d25c5"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(),
        "json/inventory/getInventoryResponse.json");
  }
}
