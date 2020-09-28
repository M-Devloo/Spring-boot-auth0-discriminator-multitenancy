package com.github.mdevloo.multi.tenancy;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.mockito.Mockito.when;

@SpringBootTest(classes = {MultiTenancyApplication.class})
@ActiveProfiles({"test"})
@SqlGroup({
  @Sql(
      scripts = "classpath:sql/cleanup.sql",
      executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
})
public abstract class AbstractIntegrationTest {

  @Autowired private TransactionalExecutor transactionalExecutor;

  protected void mockSecurityContext(final String jwtName) {
    final JwtAuthenticationToken authentication = Mockito.mock(JwtAuthenticationToken.class);
    when(authentication.getName()).thenReturn(jwtName);
    final SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
  }

  protected void transaction(final Runnable runnable) {
    this.transactionalExecutor.required(runnable);
  }
}
