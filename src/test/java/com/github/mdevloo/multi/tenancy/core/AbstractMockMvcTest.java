package com.github.mdevloo.multi.tenancy.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mdevloo.multi.tenancy.AbstractIntegrationTest;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.oauth2.jwt.JwtClaimNames.SUB;

@AutoConfigureMockMvc
@TestExecutionListeners(
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS,
    listeners = WithSecurityContextTestExecutionListener.class)
@Getter(value = AccessLevel.PROTECTED)
public class AbstractMockMvcTest extends AbstractIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ResourceLoader resourceLoader;

  @MockBean
  @SuppressWarnings("unused")
  private JwtDecoder jwtDecoder;

  protected final Jwt getJwtToken() {
    return this.getJwtToken("auth0|current");
  }

  protected final Jwt getJwtToken(final String sub) {
    return Jwt.withTokenValue("token")
        .header("alg", "none")
        .claim(SUB, sub)
        .claim("scope", "read")
        .build();
  }
}
