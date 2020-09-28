package com.github.mdevloo.multi.tenancy.core.web.inventory;

import com.github.mdevloo.multi.tenancy.core.AbstractMockMvcTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.github.mdevloo.multi.tenancy.core.AppConstants.INVENTORY_URL;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InventoryControllerApiSecurityIntegrationTest extends AbstractMockMvcTest {

  @Test
  void getInventoryHasProtectedApi_Unauthorized() throws Exception {
    this.getMockMvc()
        .perform(get(INVENTORY_URL).param("id", UUID.randomUUID().toString()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void getAllInventoryHasProtectedApi_Unauthorized() throws Exception {
    this.getMockMvc().perform(get(INVENTORY_URL + "/all")).andExpect(status().isUnauthorized());
  }

  /**
   * CSRF token is required for POST (CookieCsrfTokenRepository - Single-App application) or else
   * forbidden is returned.
   */
  @Test
  void createInventoryHasProtectedApi_WithoutCsrf() throws Exception {
    this.getMockMvc().perform(post(INVENTORY_URL + "/create")).andExpect(status().isForbidden());
  }

  @Test
  void createInventoryHasProtectedApi_Unauthorized() throws Exception {
    this.getMockMvc()
        .perform(post(INVENTORY_URL + "/create").with(csrf()))
        .andExpect(status().isUnauthorized());
  }
}
